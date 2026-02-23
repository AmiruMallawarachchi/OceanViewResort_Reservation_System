package com.oceanview.resort.controller;

import com.oceanview.resort.dto.UserDTO;
import com.oceanview.resort.factory.ServiceFactory;
import com.oceanview.resort.service.UserService;

import javax.annotation.Nonnull;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

public class AuthController extends HttpServlet {
    private final UserService userService = ServiceFactory.getInstance().getUserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null || action.isBlank()) {
            action = "login";
        }
        if ("login".equalsIgnoreCase(action)) {
            handleLogin(request, response);
        } else if ("logout".equalsIgnoreCase(action)) {
            handleLogout(request, response);
        } else if ("forgotPassword".equalsIgnoreCase(action)) {
            handleForgotPassword(request, response);
        } else if ("resetPassword".equalsIgnoreCase(action)) {
            handleResetPassword(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        java.util.Map<String, String> errors = new java.util.HashMap<>();
        if (username == null || username.isBlank()) {
            errors.put("username", "Username is required.");
        }
        if (password == null || password.isBlank()) {
            errors.put("password", "Password is required.");
        }
        if (!errors.isEmpty()) {
            request.getSession().setAttribute("fieldErrors", errors);
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        UserDTO existingUser = userService.findByUsername(username);
        if (existingUser == null) {
            request.getSession().setAttribute("flashError", "Invalid username.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        if (!existingUser.isActive()) {
            request.getSession().setAttribute("flashError", "Invalid credentials.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        UserDTO user = userService.authenticate(username, password);
        if (user == null) {
            request.getSession().setAttribute("flashError", "Invalid password.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole());

        String rememberMe = request.getParameter("rememberMe");
        if (rememberMe != null && !rememberMe.isBlank()) {
            session.setMaxInactiveInterval(7 * 24 * 60 * 60); // 7 days
            String ctx = request.getContextPath();
            Cookie sessionCookie = new Cookie("JSESSIONID", session.getId());
            sessionCookie.setMaxAge(7 * 24 * 60 * 60);
            sessionCookie.setPath(ctx != null && !ctx.isEmpty() ? ctx : "/");
            sessionCookie.setHttpOnly(true);
            if (request.isSecure()) {
                sessionCookie.setSecure(true);
            }
            response.addCookie(sessionCookie);
        }

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp");
        } else {
            response.sendRedirect(request.getContextPath() + "/reservationist/dashboard.jsp");
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        request.getSession(true).setAttribute("flashSuccess", "Logged out successfully.");
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }

    private void handleForgotPassword(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String ctx = request.getContextPath();
        if (email == null || email.isBlank()) {
            request.getSession().setAttribute("flashError", "Please enter your email address.");
            response.sendRedirect(ctx + "/forgot-password.jsp");
            return;
        }
        userService.requestPasswordResetOtp(email.trim());
        request.getSession().setAttribute("resetEmail", email.trim());
        request.getSession().setAttribute("flashSuccess",
                "If an account exists for that email, we sent a reset code. Check your inbox and enter the code below.");
        response.sendRedirect(ctx + "/reset-password.jsp");
    }

    private void handleResetPassword(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        String ctx = request.getContextPath();
        String email = request.getParameter("email");
        if ((email == null || email.isBlank()) && session != null) {
            email = (String) session.getAttribute("resetEmail");
        }
        if (email == null || email.isBlank()) {
            request.getSession().setAttribute("flashError", "Session expired. Please request a new reset code.");
            response.sendRedirect(ctx + "/forgot-password.jsp");
            return;
        }
        String otp = request.getParameter("otp");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        Map<String, String> errors = getStringStringMap(otp, newPassword, confirmPassword);
        if (!errors.isEmpty()) {
            request.getSession().setAttribute("fieldErrors", errors);
            request.getSession().setAttribute("resetEmail", email);
            response.sendRedirect(ctx + "/reset-password.jsp");
            return;
        }
        boolean ok = userService.resetPasswordWithOtp(email, otp, newPassword);
        if (session != null) {
            session.removeAttribute("resetEmail");
        }
        if (!ok) {
            request.getSession().setAttribute("flashError", "Invalid or expired code. Please request a new one.");
            response.sendRedirect(ctx + "/forgot-password.jsp");
            return;
        }
        request.getSession(true).setAttribute("flashSuccess", "Password updated. You can now sign in.");
        response.sendRedirect(ctx + "/login.jsp");
    }

    @Nonnull
    private static Map<String, String> getStringStringMap(String otp, String newPassword, String confirmPassword) {
        Map<String, String> errors = new java.util.HashMap<>();
        if (otp == null || otp.isBlank()) {
            errors.put("otp", "Reset code is required.");
        }
        if (newPassword == null || newPassword.isBlank()) {
            errors.put("newPassword", "New password is required.");
        } else if (newPassword.length() < 6) {
            errors.put("newPassword", "Password must be at least 6 characters.");
        }
        if (!java.util.Objects.equals(newPassword, confirmPassword)) {
            errors.put("confirmPassword", "Passwords do not match.");
        }
        return errors;
    }
}
