package com.oceanview.resort.controllerTest;

import com.oceanview.resort.config.AppConfig;
import com.oceanview.resort.controller.GoogleAuthController;
import com.oceanview.resort.dto.UserDTO;
import com.oceanview.resort.service.UserService;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests for GoogleAuthController.
 * These tests focus on the controller's decision logic and session / redirect
 * behavior, while stubbing out network calls and Google responses.
 */
public class GoogleAuthControllerTest {

    private static final String CONTEXT_PATH = "/OceanViewResort_Reservation_System";

    private TestableGoogleAuthController controller;
    private UserService userService;

    @Before
    public void setUp() throws Exception {
        controller = new TestableGoogleAuthController();
        userService = mock(UserService.class);
        injectUserService(controller, userService);

        // Default: Google login fully configured
        setAppProperty("google.login.enabled", "true");
        setAppProperty("google.client.id", "test-client-id");
        setAppProperty("google.client.secret", "test-client-secret");
        setAppProperty("app.base.url", "http://localhost:8080");

        controller.setTokenToReturn("access-token");
        controller.setEmailToReturn("user@example.com");
    }

    // --- Helpers ------------------------------------------------------------

    private void setAppProperty(String key, String value) throws Exception {
        Field field = AppConfig.class.getDeclaredField("PROPERTIES");
        field.setAccessible(true);
        Properties props = (Properties) field.get(null);
        if (value == null) {
            props.remove(key);
        } else {
            props.setProperty(key, value);
        }
    }

    private void injectUserService(GoogleAuthController controller, UserService userService) throws Exception {
        Field field = GoogleAuthController.class.getDeclaredField("userService");
        field.setAccessible(true);
        field.set(controller, userService);
    }

    /**
     * Test subclass that overrides network methods so tests don't hit real Google APIs.
     */
    private static class TestableGoogleAuthController extends GoogleAuthController {
        private String tokenToReturn;
        private String emailToReturn;

        void setTokenToReturn(String tokenToReturn) {
            this.tokenToReturn = tokenToReturn;
        }

        void setEmailToReturn(String emailToReturn) {
            this.emailToReturn = emailToReturn;
        }

        @Override
        protected String exchangeCodeForToken(String code, String clientId, String clientSecret, String redirectUri) {
            return tokenToReturn;
        }

        @Override
        protected String fetchEmailFromUserInfo(String accessToken) {
            return emailToReturn;
        }
    }

    // --- Tests for doGet (start + guard rails) ------------------------------

    @Test
    public void testDoGet_GoogleLoginDisabledRedirectsToLogin() throws Exception {
        setAppProperty("google.login.enabled", "false");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);

        controller.service(request, response);

        verify(response).sendRedirect(CONTEXT_PATH + "/login.jsp");
    }

    @Test
    public void testDoGet_NotConfiguredSetsFlashError() throws Exception {
        setAppProperty("google.client.id", "");
        setAppProperty("app.base.url", "");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        when(request.getSession(true)).thenReturn(session);

        controller.service(request, response);

        verify(session).setAttribute(eq("flashError"), eq("Google login is not configured."));
        verify(response).sendRedirect(CONTEXT_PATH + "/login.jsp");
    }

    @Test
    public void testDoGet_StartsOAuthRedirectsToGoogle() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        when(request.getPathInfo()).thenReturn(null);
        when(request.getSession(true)).thenReturn(session);

        controller.service(request, response);

        // state should be stored in session
        verify(session).setAttribute(eq("google_oauth_state"), anyString());
        // redirect should go to Google's auth endpoint
        verify(response).sendRedirect(org.mockito.ArgumentMatchers.argThat(url ->
                url.startsWith("https://accounts.google.com/o/oauth2/v2/auth") &&
                        url.contains("client_id=test-client-id")));
    }

    @Test
    public void testDoGet_UnknownPathRedirectsToLogin() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        when(request.getPathInfo()).thenReturn("/other");

        controller.service(request, response);

        verify(response).sendRedirect(CONTEXT_PATH + "/login.jsp");
    }

    // --- Tests for callback flow -------------------------------------------

    @Test
    public void testCallback_WithErrorParamShowsCancelledMessage() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        when(request.getPathInfo()).thenReturn("/callback");
        when(request.getParameter("error")).thenReturn("access_denied");
        when(request.getSession(true)).thenReturn(session);

        controller.service(request, response);

        verify(session).setAttribute(eq("flashError"), eq("Google sign-in was cancelled or failed."));
        verify(response).sendRedirect(CONTEXT_PATH + "/login.jsp");
    }

    @Test
    public void testCallback_InvalidStateShowsError() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession existingSession = mock(HttpSession.class);
        HttpSession newSession = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        when(request.getPathInfo()).thenReturn("/callback");
        when(request.getParameter("code")).thenReturn("code123");
        when(request.getParameter("state")).thenReturn("wrong");
        when(request.getSession(false)).thenReturn(existingSession);
        when(existingSession.getAttribute("google_oauth_state")).thenReturn("expected");
        when(request.getSession(true)).thenReturn(newSession);

        controller.service(request, response);

        verify(newSession).setAttribute(eq("flashError"), eq("Invalid Google login state. Please try again."));
        verify(response).sendRedirect(CONTEXT_PATH + "/login.jsp");
    }

    @Test
    public void testCallback_TokenExchangeFailsShowsError() throws Exception {
        controller.setTokenToReturn(null); // simulate failure to get access token

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession existingSession = mock(HttpSession.class);
        HttpSession newSession = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        when(request.getPathInfo()).thenReturn("/callback");
        when(request.getParameter("code")).thenReturn("code123");
        when(request.getParameter("state")).thenReturn("state123");
        when(request.getSession(false)).thenReturn(existingSession);
        when(existingSession.getAttribute("google_oauth_state")).thenReturn("state123");
        when(request.getSession(true)).thenReturn(newSession);

        controller.service(request, response);

        verify(newSession).setAttribute(eq("flashError"), eq("Could not sign in with Google. Please try again."));
        verify(response).sendRedirect(CONTEXT_PATH + "/login.jsp");
    }

    @Test
    public void testCallback_NoEmailFromUserInfoShowsError() throws Exception {
        controller.setTokenToReturn("access-token");
        controller.setEmailToReturn(null); // simulate missing email

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession existingSession = mock(HttpSession.class);
        HttpSession newSession = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        when(request.getPathInfo()).thenReturn("/callback");
        when(request.getParameter("code")).thenReturn("code123");
        when(request.getParameter("state")).thenReturn("state123");
        when(request.getSession(false)).thenReturn(existingSession);
        when(existingSession.getAttribute("google_oauth_state")).thenReturn("state123");
        when(request.getSession(true)).thenReturn(newSession);

        controller.service(request, response);

        verify(newSession).setAttribute(eq("flashError"), eq("Could not get your email from Google."));
        verify(response).sendRedirect(CONTEXT_PATH + "/login.jsp");
    }

    @Test
    public void testCallback_NoUserLinkedToEmailShowsError() throws Exception {
        controller.setTokenToReturn("access-token");
        controller.setEmailToReturn("missing@example.com");
        when(userService.findByEmail("missing@example.com")).thenReturn(null);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession existingSession = mock(HttpSession.class);
        HttpSession newSession = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        when(request.getPathInfo()).thenReturn("/callback");
        when(request.getParameter("code")).thenReturn("code123");
        when(request.getParameter("state")).thenReturn("state123");
        when(request.getSession(false)).thenReturn(existingSession);
        when(existingSession.getAttribute("google_oauth_state")).thenReturn("state123");
        when(request.getSession(true)).thenReturn(newSession);

        controller.service(request, response);

        verify(newSession).setAttribute(eq("flashError"),
                eq("No account is linked to this Google email. Please sign in with your username and password or contact an administrator."));
        verify(response).sendRedirect(CONTEXT_PATH + "/login.jsp");
    }

    @Test
    public void testCallback_Success_AdminRedirectsToAdminDashboard() throws Exception {
        controller.setTokenToReturn("access-token");
        controller.setEmailToReturn("admin@example.com");

        UserDTO user = new UserDTO();
        user.setId(1L);
        user.setUsername("admin");
        user.setRole("ADMIN");
        when(userService.findByEmail("admin@example.com")).thenReturn(user);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession existingSession = mock(HttpSession.class);
        HttpSession newSession = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        when(request.getPathInfo()).thenReturn("/callback");
        when(request.getParameter("code")).thenReturn("code123");
        when(request.getParameter("state")).thenReturn("state123");
        when(request.getSession(false)).thenReturn(existingSession);
        when(existingSession.getAttribute("google_oauth_state")).thenReturn("state123");
        when(request.getSession(true)).thenReturn(newSession);

        controller.service(request, response);

        verify(newSession).setAttribute("userId", 1L);
        verify(newSession).setAttribute("username", "admin");
        verify(newSession).setAttribute("role", "ADMIN");
        verify(response).sendRedirect(CONTEXT_PATH + "/admin/dashboard.jsp");
    }

    @Test
    public void testCallback_Success_ReservationistRedirectsToReservationistDashboard() throws Exception {
        controller.setTokenToReturn("access-token");
        controller.setEmailToReturn("res@example.com");

        UserDTO user = new UserDTO();
        user.setId(2L);
        user.setUsername("res");
        user.setRole("RESERVATIONIST");
        when(userService.findByEmail("res@example.com")).thenReturn(user);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession existingSession = mock(HttpSession.class);
        HttpSession newSession = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        when(request.getPathInfo()).thenReturn("/callback");
        when(request.getParameter("code")).thenReturn("code123");
        when(request.getParameter("state")).thenReturn("state123");
        when(request.getSession(false)).thenReturn(existingSession);
        when(existingSession.getAttribute("google_oauth_state")).thenReturn("state123");
        when(request.getSession(true)).thenReturn(newSession);

        controller.service(request, response);

        verify(newSession).setAttribute("userId", 2L);
        verify(newSession).setAttribute("username", "res");
        verify(newSession).setAttribute("role", "RESERVATIONIST");
        verify(response).sendRedirect(CONTEXT_PATH + "/reservationist/dashboard.jsp");
    }
}

