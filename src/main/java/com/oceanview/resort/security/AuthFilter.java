package com.oceanview.resort.security;

import com.oceanview.resort.config.AppConfig;

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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Central authentication / authorization filter.
 * All public and admin-only paths are defined as constants to keep things
 * simple to maintain and avoid long chains of string comparisons.
 */
public class AuthFilter implements Filter {

    private static final Set<String> DEFAULT_PUBLIC_PATHS = Set.of(
            "/",
            "/index.jsp",
            "/login.jsp",
            "/forgot-password.jsp",
            "/reset-password.jsp",
            "/help.jsp",
            "/about.jsp",
            "/contact.jsp",
            "/auth"
    );

    private static final Set<String> DEFAULT_PUBLIC_PREFIXES = Set.of(
            "/assets/",
            "/auth/google"
    );

    private static final Set<String> DEFAULT_ADMIN_EXACT_PATHS = Set.of(
            "/users",
            "/rooms",
            "/room-types",
            "/reports",
            "/discounts"
    );

    private static final Set<String> PUBLIC_PATHS = loadPathSet("auth.public.paths", DEFAULT_PUBLIC_PATHS);
    private static final Set<String> PUBLIC_PREFIXES = loadPathSet("auth.public.prefixes", DEFAULT_PUBLIC_PREFIXES);
    private static final Set<String> ADMIN_EXACT_PATHS = loadPathSet("auth.admin.paths", DEFAULT_ADMIN_EXACT_PATHS);
    private static final String ADMIN_PREFIX = AppConfig.getProperty("auth.admin.prefix", "/admin/");

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String contextPath = req.getContextPath();
        String path = req.getRequestURI().substring(contextPath.length());

        if (isPublic(path)) {
            if ("/login.jsp".equals(path)) {
                req.setAttribute("googleLoginEnabled",
                        AppConfig.getBoolean("google.login.enabled", false));
            }
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        Object userId = session == null ? null : session.getAttribute("userId");
        String role = session == null ? null : (String) session.getAttribute("role");

        if (userId == null) {
            res.sendRedirect(contextPath + "/login.jsp");
            return;
        }

        if (isAdminOnly(path) && !"ADMIN".equalsIgnoreCase(role)) {
            res.sendRedirect(contextPath + "/reservationist/dashboard.jsp");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    private boolean isPublic(String path) {
        if (PUBLIC_PATHS.contains(path)) {
            return true;
        }
        for (String prefix : PUBLIC_PREFIXES) {
            if (path.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAdminOnly(String path) {
        if (path.startsWith(ADMIN_PREFIX)) {
            return true;
        }
        return ADMIN_EXACT_PATHS.contains(path);
    }

    private static Set<String> loadPathSet(String key, Set<String> defaultValues) {
        String configured = AppConfig.getProperty(key);
        if (configured == null || configured.isBlank()) {
            return defaultValues;
        }
        String[] parts = configured.split(",");
        Set<String> values = new HashSet<>();
        for (String part : parts) {
            if (part != null) {
                String trimmed = part.trim();
                if (!trimmed.isEmpty()) {
                    values.add(trimmed);
                }
            }
        }
        return values.isEmpty() ? defaultValues : Collections.unmodifiableSet(values);
    }
}
