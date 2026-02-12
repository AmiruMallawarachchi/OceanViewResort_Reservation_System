package com.oceanview.resort.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oceanview.resort.config.AppConfig;
import com.oceanview.resort.dto.UserDTO;
import com.oceanview.resort.factory.ServiceFactory;
import com.oceanview.resort.service.UserService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Scanner;


public class GoogleAuthController extends HttpServlet {

    private static final String AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String USERINFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo";
    private static final String SCOPES = "openid email profile";
    private static final String SESSION_STATE_KEY = "google_oauth_state";

    private final UserService userService = ServiceFactory.getInstance().getUserService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!AppConfig.getBoolean("google.login.enabled", false)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String clientId = AppConfig.getProperty("google.client.id", "").trim();
        String clientSecret = AppConfig.getProperty("google.client.secret", "").trim();
        String baseUrl = AppConfig.getProperty("app.base.url", "").trim();
        if (clientId.isEmpty() || baseUrl.isEmpty()) {
            request.getSession(true).setAttribute("flashError", "Google login is not configured.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.isEmpty() || "/".equals(pathInfo)) {
            // Start OAuth: redirect to Google
            String redirectUri = baseUrl + request.getContextPath() + "/auth/google/callback";
            String state = generateState();
            request.getSession(true).setAttribute(SESSION_STATE_KEY, state);

            String url = AUTH_URL
                    + "?client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
                    + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                    + "&response_type=code"
                    + "&scope=" + URLEncoder.encode(SCOPES, StandardCharsets.UTF_8)
                    + "&state=" + URLEncoder.encode(state, StandardCharsets.UTF_8)
                    + "&access_type=online";
            response.sendRedirect(url);
            return;
        }

        if ("/callback".equals(pathInfo)) {
            handleCallback(request, response, clientId, clientSecret, baseUrl);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }

    private void handleCallback(HttpServletRequest request, HttpServletResponse response,
                                String clientId, String clientSecret, String baseUrl) throws IOException {
        String code = request.getParameter("code");
        String stateParam = request.getParameter("state");
        String error = request.getParameter("error");

        if (error != null) {
            request.getSession(true).setAttribute("flashError", "Google sign-in was cancelled or failed.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        HttpSession session = request.getSession(false);
        String stateStored = session == null ? null : (String) session.getAttribute(SESSION_STATE_KEY);
        if (stateStored == null || !stateStored.equals(stateParam) || code == null || code.isBlank()) {
            request.getSession(true).setAttribute("flashError", "Invalid Google login state. Please try again.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        session.removeAttribute(SESSION_STATE_KEY);

        String redirectUri = baseUrl + request.getContextPath() + "/auth/google/callback";
        String accessToken = exchangeCodeForToken(code, clientId, clientSecret, redirectUri);
        if (accessToken == null) {
            request.getSession(true).setAttribute("flashError", "Could not sign in with Google. Please try again.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String email = fetchEmailFromUserInfo(accessToken);
        if (email == null || email.isBlank()) {
            request.getSession(true).setAttribute("flashError", "Could not get your email from Google.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        UserDTO user = userService.findByEmail(email);
        if (user == null) {
            request.getSession(true).setAttribute("flashError",
                    "No account is linked to this Google email. Please sign in with your username and password or contact an administrator.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        HttpSession newSession = request.getSession(true);
        newSession.setAttribute("userId", user.getId());
        newSession.setAttribute("username", user.getUsername());
        newSession.setAttribute("role", user.getRole());

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp");
        } else {
            response.sendRedirect(request.getContextPath() + "/reservationist/dashboard.jsp");
        }
    }

    private String generateState() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[24];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    protected String exchangeCodeForToken(String code, String clientId, String clientSecret, String redirectUri) throws IOException {
        String form = "code=" + URLEncoder.encode(code, StandardCharsets.UTF_8)
                + "&client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
                + "&client_secret=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8)
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                + "&grant_type=authorization_code";

        HttpURLConnection conn = (HttpURLConnection) new URL(TOKEN_URL).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(form.getBytes(StandardCharsets.UTF_8));
        }

        int status = conn.getResponseCode();
        InputStream is = status >= 200 && status < 300 ? conn.getInputStream() : conn.getErrorStream();
        if (is == null) return null;
        String body = new Scanner(is, StandardCharsets.UTF_8).useDelimiter("\\A").next();
        is.close();

        if (status != 200) return null;
        JsonNode root = objectMapper.readTree(body);
        JsonNode token = root.get("access_token");
        return token != null ? token.asText() : null;
    }

    protected String fetchEmailFromUserInfo(String accessToken) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(USERINFO_URL).openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);

        int status = conn.getResponseCode();
        InputStream is = status == 200 ? conn.getInputStream() : conn.getErrorStream();
        if (is == null) return null;
        String body = new Scanner(is, StandardCharsets.UTF_8).useDelimiter("\\A").next();
        is.close();

        if (status != 200) return null;
        JsonNode root = objectMapper.readTree(body);
        JsonNode email = root.get("email");
        return email != null ? email.asText() : null;
    }
}
