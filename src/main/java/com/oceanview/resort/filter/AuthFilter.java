package com.oceanview.resort.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI().substring(req.getContextPath().length());
        if (isPublic(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        Object userId = session == null ? null : session.getAttribute("userId");
        String role = session == null ? null : (String) session.getAttribute("role");

        if (userId == null) {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        if (isAdminOnly(path) && !"ADMIN".equalsIgnoreCase(role)) {
            res.sendRedirect(req.getContextPath() + "/reservationist/dashboard.jsp");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    private boolean isPublic(String path) {
        return path.equals("/") ||
                path.equals("/index.jsp") ||
                path.equals("/login.jsp") ||
                path.equals("/help.jsp") ||
                path.equals("/about.jsp") ||
                path.equals("/contact.jsp") ||
                path.startsWith("/assets/") ||
                path.equals("/auth");
    }

    private boolean isAdminOnly(String path) {
        return path.startsWith("/admin/") ||
                path.equals("/users") ||
                path.equals("/rooms") ||
                path.equals("/room-types") ||
                path.equals("/reports") ||
                path.equals("/discounts");
    }
}
