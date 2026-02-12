package com.oceanview.resort.controllerTest;

import com.oceanview.resort.controller.UserController;
import com.oceanview.resort.config.DatabaseConnection;
import com.oceanview.resort.security.PasswordUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.RequestDispatcher;
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
 * Basic tests for UserController: list users (GET) and create user validation (POST).
 * These tests focus on the controller behavior (forward / redirect and session attributes),
 * not on the details of UserService or the database.
 */
public class UserControllerTest {

    private UserController userController;
    private static final String CONTEXT_PATH = "/OceanViewResort_Reservation_System";
    private static final String USERNAME_PREFIX = "userctrl_test_";

    @Before
    public void setup() {
        userController = new UserController();
    }

    @After
    public void tearDown() {
        // Clean up any users created for these tests
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE username LIKE ?")) {
            stmt.setString(1, USERNAME_PREFIX + "%");
            stmt.executeUpdate();
        } catch (SQLException ignored) {
        }
    }

    @Test
    public void testDoGet_ForwardsToUsersPage() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestDispatcher("/admin/users.jsp")).thenReturn(dispatcher);

        userController.service(request, response);

        verify(request).getRequestDispatcher("/admin/users.jsp");
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void testPostCreateUser_WithMissingFields_SetsFieldErrorsAndRedirects() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn(null); // create path
        when(request.getParameter("fullName")).thenReturn(""); // missing full name
        when(request.getParameter("username")).thenReturn(""); // missing username
        when(request.getParameter("email")).thenReturn("");    // optional but kept blank
        when(request.getParameter("role")).thenReturn("");     // missing role
        when(request.getParameter("password")).thenReturn(""); // missing password
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        when(request.getSession(anyBoolean())).thenReturn(session);
        when(request.getSession()).thenReturn(session);

        userController.service(request, response);

        verify(session).setAttribute(eq("fieldErrors"), org.mockito.ArgumentMatchers.argThat(errors -> {
            java.util.Map<?, ?> map = (java.util.Map<?, ?>) errors;
            return map.get("fullName") != null
                    && map.get("username") != null
                    && map.get("role") != null
                    && map.get("password") != null;
        }));
        verify(response).sendRedirect(CONTEXT_PATH + "/users");
    }

    @Test
    public void testDoGet_WithEditIdLoadsEditUser() throws ServletException, IOException {
        long userId = createTestUser();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getParameter("editId")).thenReturn(String.valueOf(userId));
        when(request.getRequestDispatcher("/admin/users.jsp")).thenReturn(dispatcher);

        userController.service(request, response);

        verify(request).setAttribute(eq("editUser"), org.mockito.ArgumentMatchers.any());
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void testDoGet_WithInvalidEditIdStillForwards() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getParameter("editId")).thenReturn("abc"); // NumberFormatException branch
        when(request.getRequestDispatcher("/admin/users.jsp")).thenReturn(dispatcher);

        userController.service(request, response);

        verify(dispatcher).forward(request, response);
    }

    @Test
    public void testDoGet_WithSearchAndFilters() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getParameter("q")).thenReturn("admin");
        when(request.getParameter("role")).thenReturn("ADMIN");
        when(request.getParameter("status")).thenReturn("ACTIVE");
        when(request.getRequestDispatcher("/admin/users.jsp")).thenReturn(dispatcher);

        userController.service(request, response);

        verify(dispatcher).forward(request, response);
    }

    @Test
    public void testPostDeleteUser_Success() throws ServletException, IOException {
        long userId = createTestUser();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("id")).thenReturn(String.valueOf(userId));
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        when(request.getSession(anyBoolean())).thenReturn(session);
        when(request.getSession()).thenReturn(session);

        userController.service(request, response);

        verify(session).setAttribute(eq("flashSuccess"), eq("User deleted successfully."));
        verify(response).sendRedirect(CONTEXT_PATH + "/users");
    }

    @Test
    public void testPostDeleteUser_InvalidIdSetsFlashError() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("id")).thenReturn("not-a-number");
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        when(request.getSession()).thenReturn(session);

        userController.service(request, response);

        verify(session).setAttribute(eq("flashError"), org.mockito.ArgumentMatchers.anyString());
        verify(response).sendRedirect(CONTEXT_PATH + "/users");
    }

    @Test
    public void testPostUpdateUser_ValidationFails() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("fullName")).thenReturn(""); // invalid
        when(request.getParameter("username")).thenReturn(""); // invalid
        when(request.getParameter("email")).thenReturn("");
        when(request.getParameter("role")).thenReturn("");     // invalid
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        when(request.getSession(anyBoolean())).thenReturn(session);
        when(request.getSession()).thenReturn(session);

        userController.service(request, response);

        verify(session).setAttribute(eq("fieldErrors"), org.mockito.ArgumentMatchers.argThat(errors -> {
            java.util.Map<?, ?> map = (java.util.Map<?, ?>) errors;
            return map.get("fullName") != null
                    && map.get("username") != null
                    && map.get("role") != null;
        }));
        verify(response).sendRedirect(CONTEXT_PATH + "/users?editId=1");
    }

    @Test
    public void testPostUpdateUser_Success() throws ServletException, IOException {
        long userId = createTestUser();
        String newUsername = USERNAME_PREFIX + "updated_" + System.nanoTime();
        String newEmail = "updated_" + System.nanoTime() + "@test.com";

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("id")).thenReturn(String.valueOf(userId));
        when(request.getParameter("fullName")).thenReturn("Updated Name");
        when(request.getParameter("username")).thenReturn(newUsername);
        when(request.getParameter("email")).thenReturn(newEmail);
        when(request.getParameter("role")).thenReturn("ADMIN");
        when(request.getParameter("active")).thenReturn("true");
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        when(request.getSession(anyBoolean())).thenReturn(session);
        when(request.getSession()).thenReturn(session);

        userController.service(request, response);

        verify(session).setAttribute(eq("flashSuccess"), eq("User updated successfully."));
        verify(response).sendRedirect(CONTEXT_PATH + "/users");
    }

    @Test
    public void testPostCreateUser_Success() throws ServletException, IOException {
        String username = USERNAME_PREFIX + "create_" + System.nanoTime();
        String email = "create_" + System.nanoTime() + "@test.com";

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn(null); // create path
        when(request.getParameter("fullName")).thenReturn("Create User");
        when(request.getParameter("username")).thenReturn(username);
        when(request.getParameter("email")).thenReturn(email);
        when(request.getParameter("role")).thenReturn("RESERVATIONIST");
        when(request.getParameter("password")).thenReturn("Strong123!");
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        when(request.getSession(anyBoolean())).thenReturn(session);
        when(request.getSession()).thenReturn(session);

        userController.service(request, response);

        verify(session).setAttribute(eq("flashSuccess"), eq("User created successfully."));
        verify(response).sendRedirect(CONTEXT_PATH + "/users");
    }

    @Test
    public void testPostCreateUser_DuplicateUsernameSetsError() throws ServletException, IOException {
        String username = USERNAME_PREFIX + "dup_" + System.nanoTime();
        String email = "dup_" + System.nanoTime() + "@test.com";
        // Create existing user with this username
        createSpecificUser(username, email);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn(null); // create path
        when(request.getParameter("fullName")).thenReturn("Duplicate User");
        when(request.getParameter("username")).thenReturn(username); // same username
        when(request.getParameter("email")).thenReturn("other_" + email);
        when(request.getParameter("role")).thenReturn("ADMIN");
        when(request.getParameter("password")).thenReturn("Strong123!");
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        when(request.getSession(anyBoolean())).thenReturn(session);
        when(request.getSession()).thenReturn(session);

        userController.service(request, response);

        verify(session).setAttribute(eq("fieldErrors"), org.mockito.ArgumentMatchers.argThat(errors -> {
            java.util.Map<?, ?> map = (java.util.Map<?, ?>) errors;
            Object msg = map.get("username");
            return msg != null && msg.toString().contains("Username already exists");
        }));
        verify(response).sendRedirect(CONTEXT_PATH + "/users");
    }

    /**
     * Creates a test user with a unique username/email and returns its id.
     */
    private long createTestUser() {
        String username = USERNAME_PREFIX + System.nanoTime();
        String email = "user_" + System.nanoTime() + "@test.com";
        return createSpecificUser(username, email);
    }

    private long createSpecificUser(String username, String email) {
        String sql = "INSERT INTO users (username, password_hash, full_name, email, role, is_active) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, username);
            stmt.setString(2, PasswordUtil.hashPassword("Pass123!"));
            stmt.setString(3, "UserController Test User");
            stmt.setString(4, email);
            stmt.setString(5, "RESERVATIONIST");
            stmt.setBoolean(6, true);
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to create test user for UserControllerTest", ex);
        }
        throw new RuntimeException("Failed to generate ID for test user");
    }
}
