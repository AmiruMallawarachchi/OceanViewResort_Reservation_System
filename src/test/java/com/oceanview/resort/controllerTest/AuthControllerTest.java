package com.oceanview.resort.controllerTest;

import com.oceanview.resort.controller.AuthController;
import com.oceanview.resort.config.DatabaseConnection;
import com.oceanview.resort.security.PasswordUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for login and logout (AuthController).
 */
public class AuthControllerTest {
    private AuthController authController;
    private static final String CONTEXT_PATH = "/OceanViewResort_Reservation_System";
    private static final String TEST_USERNAME = "logintest_" + System.currentTimeMillis();
    private static final String TEST_PASSWORD = "Test123!@#";
    private long testUserId;

    @Before
    public void setup() {
        authController = new AuthController();
        createTestUser();
    }

    @After
    public void tearDown() {
        deleteTestUser();
    }

    @Test
    public void testLoginSuccess_AdminRedirectsToAdminDashboard() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("login");
        when(request.getParameter("username")).thenReturn(TEST_USERNAME);
        when(request.getParameter("password")).thenReturn(TEST_PASSWORD);
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        when(request.getSession(anyBoolean())).thenReturn(session);
        when(request.getSession()).thenReturn(session);

        authController.service(request, response);

        verify(session).setAttribute(eq("userId"), eq(testUserId));
        verify(session).setAttribute(eq("username"), eq(TEST_USERNAME));
        verify(session).setAttribute(eq("role"), eq("ADMIN"));
        verify(response).sendRedirect(CONTEXT_PATH + "/admin/dashboard.jsp");
    }

    @Test
    public void testLoginSuccess_NullActionDefaultsToLogin() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        // action is null -> controller should treat it as \"login\"
        when(request.getParameter("action")).thenReturn(null);
        when(request.getParameter("username")).thenReturn(TEST_USERNAME);
        when(request.getParameter("password")).thenReturn(TEST_PASSWORD);
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        when(request.getSession(anyBoolean())).thenReturn(session);
        when(request.getSession()).thenReturn(session);

        authController.service(request, response);

        verify(session).setAttribute(eq("userId"), eq(testUserId));
        verify(session).setAttribute(eq("username"), eq(TEST_USERNAME));
        verify(session).setAttribute(eq("role"), eq("ADMIN"));
        verify(response).sendRedirect(CONTEXT_PATH + "/admin/dashboard.jsp");
    }

    @Test
    public void testLoginSuccess_ReservationistRedirectsToReservationistDashboard() throws IOException, ServletException {
        setTestUserRole();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("login");
        when(request.getParameter("username")).thenReturn(TEST_USERNAME);
        when(request.getParameter("password")).thenReturn(TEST_PASSWORD);
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        when(request.getSession(anyBoolean())).thenReturn(session);
        when(request.getSession()).thenReturn(session);

        authController.service(request, response);

        verify(session).setAttribute(eq("userId"), eq(testUserId));
        verify(session).setAttribute(eq("role"), eq("RESERVATIONIST"));
        verify(response).sendRedirect(CONTEXT_PATH + "/reservationist/dashboard.jsp");
    }

    @Test
    public void testLoginInvalidUsername() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("login");
        when(request.getParameter("username")).thenReturn("wronguser");
        when(request.getParameter("password")).thenReturn("admin123");
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        when(request.getSession(anyBoolean())).thenReturn(session);
        when(request.getSession()).thenReturn(session);

        authController.service(request, response);

        verify(session).setAttribute(eq("flashError"), eq("Invalid username."));
        verify(response).sendRedirect(CONTEXT_PATH + "/login.jsp");
    }

    @Test
    public void testLoginInvalidPassword() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("login");
        when(request.getParameter("username")).thenReturn(TEST_USERNAME);
        when(request.getParameter("password")).thenReturn("wrongpass");
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        when(request.getSession(anyBoolean())).thenReturn(session);
        when(request.getSession()).thenReturn(session);

        authController.service(request, response);

        verify(session).setAttribute(eq("flashError"), eq("Invalid password."));
        verify(response).sendRedirect(CONTEXT_PATH + "/login.jsp");
    }

    @Test
    public void testLoginBlankUsername() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("login");
        when(request.getParameter("username")).thenReturn("");
        when(request.getParameter("password")).thenReturn(TEST_PASSWORD);
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        when(request.getSession(anyBoolean())).thenReturn(session);
        when(request.getSession()).thenReturn(session);

        authController.service(request, response);

        verify(session).setAttribute(eq("fieldErrors"), org.mockito.ArgumentMatchers.argThat(errors ->
                "Username is required.".equals(((java.util.Map<?, ?>) errors).get("username"))));
        verify(response).sendRedirect(CONTEXT_PATH + "/login.jsp");
    }

    @Test
    public void testLoginBothFieldsEmpty() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("login");
        when(request.getParameter("username")).thenReturn("");
        when(request.getParameter("password")).thenReturn("");
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        when(request.getSession(anyBoolean())).thenReturn(session);
        when(request.getSession()).thenReturn(session);

        authController.service(request, response);

        verify(session).setAttribute(eq("fieldErrors"), org.mockito.ArgumentMatchers.argThat(errors -> {
            java.util.Map<?, ?> map = (java.util.Map<?, ?>) errors;
            return "Username is required.".equals(map.get("username"))
                    && "Password is required.".equals(map.get("password"));
        }));
        verify(response).sendRedirect(CONTEXT_PATH + "/login.jsp");
    }

    @Test
    public void testLoginBlankPassword() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("login");
        when(request.getParameter("username")).thenReturn(TEST_USERNAME);
        when(request.getParameter("password")).thenReturn("");
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        when(request.getSession(anyBoolean())).thenReturn(session);
        when(request.getSession()).thenReturn(session);

        authController.service(request, response);

        verify(session).setAttribute(eq("fieldErrors"), org.mockito.ArgumentMatchers.argThat(errors ->
                "Password is required.".equals(((java.util.Map<?, ?>) errors).get("password"))));
        verify(response).sendRedirect(CONTEXT_PATH + "/login.jsp");
    }

    @Test
    public void testLogout() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        HttpSession newSession = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("logout");
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession(true)).thenReturn(newSession);

        authController.service(request, response);

        verify(session).invalidate();
        verify(newSession).setAttribute(eq("flashSuccess"), eq("Logged out successfully."));
        verify(response).sendRedirect(CONTEXT_PATH + "/login.jsp");
    }

    @Test
    public void testLogoutWithoutExistingSession() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession newSession = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("logout");
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        // No existing session
        when(request.getSession(false)).thenReturn(null);
        when(request.getSession(true)).thenReturn(newSession);

        authController.service(request, response);

        // Should not throw and should still create a new session with flash message
        verify(newSession).setAttribute(eq("flashSuccess"), eq("Logged out successfully."));
        verify(response).sendRedirect(CONTEXT_PATH + "/login.jsp");
    }

    @Test
    public void testUnknownActionRedirectsToLogin() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("something-else");
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);

        authController.service(request, response);

        verify(response).sendRedirect(CONTEXT_PATH + "/login.jsp");
    }

    @Test
    public void testDoGetRedirectsToLogin() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);

        authController.service(request, response);

        verify(response).sendRedirect(CONTEXT_PATH + "/login.jsp");
    }

    private void createTestUser() {
        String hash = PasswordUtil.hashPassword(TEST_PASSWORD);
        String sql = "INSERT INTO users (username, password_hash, full_name, email, role, is_active) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, TEST_USERNAME);
            stmt.setString(2, hash);
            stmt.setString(3, "Login Test User");
            stmt.setString(4, "logintest@test.com");
            stmt.setString(5, "ADMIN");
            stmt.setBoolean(6, true);
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    testUserId = keys.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create test user", e);
        }
    }

    private void setTestUserRole() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE users SET role = ? WHERE id = ?")) {
            stmt.setString(1, "RESERVATIONIST");
            stmt.setLong(2, testUserId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update test user role", e);
        }
    }

    private void deleteTestUser() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE username = ?")) {
            stmt.setString(1, TEST_USERNAME);
            stmt.executeUpdate();
        } catch (SQLException e) {
            // ignore
        }
    }
}
