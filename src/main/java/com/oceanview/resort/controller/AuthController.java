package com.oceanview.resort.controller;

import com.oceanview.resort.dto.UserDTO;
import com.oceanview.resort.factory.ServiceFactory;
import com.oceanview.resort.service.UserService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

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

        UserDTO user = userService.authenticate(username, password);
        if (user == null) {
            request.getSession().setAttribute("flashError", "Invalid credentials.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole());

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
}
