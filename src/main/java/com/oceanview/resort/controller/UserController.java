package com.oceanview.resort.controller;

import com.oceanview.resort.dto.UserDTO;
import com.oceanview.resort.factory.ServiceFactory;
import com.oceanview.resort.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class UserController extends HttpServlet {
    private final UserService userService = ServiceFactory.getInstance().getUserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String editId = request.getParameter("editId");
        if (editId != null) {
            try {
                long id = Long.parseLong(editId);
                UserDTO editUser = userService.findById(id);
                request.setAttribute("editUser", editUser);
            } catch (NumberFormatException ignored) {
            }
        }
        String searchQuery = trimToNull(request.getParameter("q"));
        String roleFilter = trimToNull(request.getParameter("role"));
        String statusFilter = trimToNull(request.getParameter("status"));
        List<UserDTO> users = userService.findAll();
        if (searchQuery != null || roleFilter != null || statusFilter != null) {
            users = filterUsers(users, searchQuery, roleFilter, statusFilter);
        }
        request.setAttribute("users", users);
        request.getRequestDispatcher("/admin/users.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        java.util.Map<String, String> errors = new java.util.HashMap<>();
        try {
            if ("delete".equalsIgnoreCase(action)) {
                long id = Long.parseLong(request.getParameter("id"));
                userService.deleteUser(id);
                request.getSession().setAttribute("flashSuccess", "User deleted successfully.");
            } else if ("sendResetPassword".equalsIgnoreCase(action)) {
                long id = Long.parseLong(request.getParameter("id"));
                UserDTO user = userService.findById(id);
                if (user != null && user.getEmail() != null && !user.getEmail().isBlank()) {
                    userService.requestPasswordResetOtp(user.getEmail());
                    request.getSession().setAttribute("flashSuccess", "Password reset email sent to " + user.getEmail() + ".");
                } else {
                    request.getSession().setAttribute("flashError", "User has no email; cannot send reset.");
                }
                response.sendRedirect(request.getContextPath() + "/users?editId=" + id);
                return;
            } else if ("update".equalsIgnoreCase(action)) {
                long id = Long.parseLong(request.getParameter("id"));
                String fullName = request.getParameter("fullName");
                String username = request.getParameter("username");
                String email = request.getParameter("email");
                if (isBlank(fullName)) {
                    errors.put("fullName", "Full name is required.");
                } else if (isValidFullName(fullName)) {
                    errors.put("fullName", "Full name must contain letters and spaces only.");
                }
                if (isBlank(username)) {
                    errors.put("username", "Username is required.");
                } else {
                    UserDTO existing = userService.findByUsername(username);
                    if (existing != null && existing.getId() != id) {
                        errors.put("username", "Username already exists.");
                    }
                }
                if (!isBlank(email)) {
                    UserDTO existingEmail = userService.findByEmail(email);
                    if (existingEmail != null && existingEmail.getId() != id) {
                        errors.put("email", "Email already exists.");
                    }
                }
                if (isBlank(request.getParameter("role"))) {
                    errors.put("role", "Role is required.");
                }
                if (!errors.isEmpty()) {
                    request.getSession().setAttribute("fieldErrors", errors);
                    response.sendRedirect(request.getContextPath() + "/users?editId=" + request.getParameter("id"));
                    return;
                }
                UserDTO dto = new UserDTO();
                dto.setId(id);
                dto.setFullName(fullName);
                dto.setUsername(username);
                dto.setEmail(email);
                dto.setRole(request.getParameter("role"));
                dto.setActive(Boolean.parseBoolean(request.getParameter("active")));
                userService.updateUser(dto);
                request.getSession().setAttribute("flashSuccess", "User updated successfully.");
            } else {
                String fullName = request.getParameter("fullName");
                String username = request.getParameter("username");
                String email = request.getParameter("email");
                if (isBlank(fullName)) {
                    errors.put("fullName", "Full name is required.");
                } else if (isValidFullName(fullName)) {
                    errors.put("fullName", "Full name must contain letters and spaces only.");
                }
                if (isBlank(username)) {
                    errors.put("username", "Username is required.");
                } else {
                    UserDTO existing = userService.findByUsername(username);
                    if (existing != null) {
                        errors.put("username", "Username already exists.");
                    }
                }
                if (!isBlank(email)) {
                    UserDTO existingEmail = userService.findByEmail(email);
                    if (existingEmail != null) {
                        errors.put("email", "Email already exists.");
                    }
                }
                if (isBlank(request.getParameter("role"))) {
                    errors.put("role", "Role is required.");
                }
                String password = request.getParameter("password");
                if (isBlank(password)) {
                    errors.put("password", "Password is required.");
                } else if (!isStrongPassword(password)) {
                    errors.put("password", "Password must be at least 8 characters and include at least two of: uppercase letters, lowercase letters, numbers, or special characters.");
                }
                if (!errors.isEmpty()) {
                    request.getSession().setAttribute("fieldErrors", errors);
                    response.sendRedirect(request.getContextPath() + "/users");
                    return;
                }
                UserDTO dto = new UserDTO();
                dto.setFullName(fullName);
                dto.setUsername(username);
                dto.setEmail(email);
                dto.setRole(request.getParameter("role"));
                dto.setActive(true);
                userService.createUser(dto, password);
                request.getSession().setAttribute("flashSuccess", "User created successfully.");
            }
        } catch (Exception ex) {
            request.getSession().setAttribute("flashError", ex.getMessage());
        }
        response.sendRedirect(request.getContextPath() + "/users");
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean isValidFullName(String value) {
        String trimmed = value == null ? "" : value.trim();
        return trimmed.isEmpty() || !trimmed.matches("[A-Za-z ]+");
    }

    private List<UserDTO> filterUsers(List<UserDTO> users, String searchQuery, String roleFilter, String statusFilter) {
        if (users == null) {
            return null;
        }
        String query = searchQuery == null ? null : searchQuery.toLowerCase();
        List<UserDTO> filtered = new java.util.ArrayList<>();
        for (UserDTO user : users) {
            if (user == null) {
                continue;
            }
            if (roleFilter != null && (user.getRole() == null || !user.getRole().equalsIgnoreCase(roleFilter))) {
                continue;
            }
            if (statusFilter != null) {
                boolean isActive = user.isActive();
                if ("ACTIVE".equalsIgnoreCase(statusFilter) && !isActive) {
                    continue;
                }
                if ("INACTIVE".equalsIgnoreCase(statusFilter) && isActive) {
                    continue;
                }
            }
            if (query != null && !matchesSearch(user, query)) {
                continue;
            }
            filtered.add(user);
        }
        return filtered;
    }

    private boolean matchesSearch(UserDTO user, String query) {
        return containsIgnoreCase(user.getFullName(), query) ||
                containsIgnoreCase(user.getUsername(), query) ||
                containsIgnoreCase(user.getEmail(), query);
    }

    private boolean containsIgnoreCase(String value, String query) {
        return value != null && value.toLowerCase().contains(query);
    }

    private boolean isStrongPassword(String password) {
        if (password == null) {
            return false;
        }
        boolean hasLength = password.length() >= 8;
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.matches(".*[^A-Za-z0-9].*");

        // Always require minimum length
        if (!hasLength) {
            return false;
        }

        int complexity = 0;
        if (hasLower) complexity++;
        if (hasUpper) complexity++;
        if (hasDigit) complexity++;
        if (hasSpecial) complexity++;

        // Allow \"good\" and \"strong\": at least two of the four character classes
        return complexity >= 2;
    }
}
