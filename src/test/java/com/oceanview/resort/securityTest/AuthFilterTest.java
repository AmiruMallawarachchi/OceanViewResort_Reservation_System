package com.oceanview.resort.securityTest;

import com.oceanview.resort.security.AuthFilter;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

public class AuthFilterTest {

    private static final String CONTEXT_PATH = "/OceanViewResort_Reservation_System";

    private AuthFilter filter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain chain;

    @Before
    public void setUp() {
        filter = new AuthFilter();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);
    }

    private void setupRequestUri(String path) {
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + path);
    }

    // --- init / destroy (no-op) ---

    @Test
    public void init_doesNotThrow() {
        FilterConfig config = mock(FilterConfig.class);
        filter.init(config);
    }

    @Test
    public void destroy_doesNotThrow() {
        filter.destroy();
    }

    // --- Public paths: exact match ---

    @Test
    public void doFilter_publicPathRoot_continuesChain() throws IOException, ServletException {
        setupRequestUri("/");
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(same(request), same(response));
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    public void doFilter_publicPathIndexJsp_continuesChain() throws IOException, ServletException {
        setupRequestUri("/index.jsp");
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(same(request), same(response));
    }

    @Test
    public void doFilter_publicPathLoginJsp_setsGoogleLoginEnabledAndContinues() throws IOException, ServletException {
        setupRequestUri("/login.jsp");
        filter.doFilter(request, response, chain);
        verify(request).setAttribute(eq("googleLoginEnabled"), any(Boolean.class));
        verify(chain).doFilter(same(request), same(response));
    }

    @Test
    public void doFilter_publicPathAuth_continuesChain() throws IOException, ServletException {
        setupRequestUri("/auth");
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(same(request), same(response));
    }

    // --- Public paths: prefix match ---

    @Test
    public void doFilter_publicPathAssetsPrefix_continuesChain() throws IOException, ServletException {
        setupRequestUri("/assets/css/style.css");
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(same(request), same(response));
    }

    @Test
    public void doFilter_publicPathAuthGooglePrefix_continuesChain() throws IOException, ServletException {
        setupRequestUri("/auth/google/callback");
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(same(request), same(response));
    }

    // --- Not public: no session or no userId → redirect to login ---

    @Test
    public void doFilter_notPublicNoSession_redirectsToLogin() throws IOException, ServletException {
        setupRequestUri("/admin/dashboard.jsp");
        when(request.getSession(false)).thenReturn(null);
        filter.doFilter(request, response, chain);
        verify(response).sendRedirect(CONTEXT_PATH + "/login.jsp");
        verify(chain, never()).doFilter(same(request), same(response));
    }

    @Test
    public void doFilter_notPublicSessionNoUserId_redirectsToLogin() throws IOException, ServletException {
        setupRequestUri("/reservationist/dashboard.jsp");
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(null);
        when(session.getAttribute("role")).thenReturn("RESERVATIONIST");
        filter.doFilter(request, response, chain);
        verify(response).sendRedirect(CONTEXT_PATH + "/login.jsp");
        verify(chain, never()).doFilter(same(request), same(response));
    }

    // --- Authenticated user, non-admin path → continue ---

    @Test
    public void doFilter_authenticatedNonAdminPath_continuesChain() throws IOException, ServletException {
        setupRequestUri("/reservationist/dashboard.jsp");
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1L);
        when(session.getAttribute("role")).thenReturn("RESERVATIONIST");
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(same(request), same(response));
        verify(response, never()).sendRedirect(anyString());
    }

    // --- Admin path with ADMIN role → continue ---

    @Test
    public void doFilter_adminPrefixPathWithAdminRole_continuesChain() throws IOException, ServletException {
        setupRequestUri("/admin/dashboard.jsp");
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1L);
        when(session.getAttribute("role")).thenReturn("ADMIN");
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(same(request), same(response));
    }

    @Test
    public void doFilter_adminExactPathWithAdminRole_continuesChain() throws IOException, ServletException {
        setupRequestUri("/users");
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1L);
        when(session.getAttribute("role")).thenReturn("ADMIN");
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(same(request), same(response));
    }

    @Test
    public void doFilter_adminRoleCaseInsensitive_continuesChain() throws IOException, ServletException {
        setupRequestUri("/admin/dashboard.jsp");
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1L);
        when(session.getAttribute("role")).thenReturn("admin");
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(same(request), same(response));
    }

    // --- Admin path with non-ADMIN role → redirect to reservationist dashboard ---

    @Test
    public void doFilter_adminPrefixPathWithNonAdminRole_redirectsToReservationistDashboard() throws IOException, ServletException {
        setupRequestUri("/admin/dashboard.jsp");
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1L);
        when(session.getAttribute("role")).thenReturn("RESERVATIONIST");
        filter.doFilter(request, response, chain);
        verify(response).sendRedirect(CONTEXT_PATH + "/reservationist/dashboard.jsp");
        verify(chain, never()).doFilter(same(request), same(response));
    }

    @Test
    public void doFilter_adminExactPathWithNonAdminRole_redirectsToReservationistDashboard() throws IOException, ServletException {
        setupRequestUri("/users");
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1L);
        when(session.getAttribute("role")).thenReturn("RESERVATIONIST");
        filter.doFilter(request, response, chain);
        verify(response).sendRedirect(CONTEXT_PATH + "/reservationist/dashboard.jsp");
        verify(chain, never()).doFilter(same(request), same(response));
    }

    // --- Other public exact paths (isPublic branch coverage) ---

    @Test
    public void doFilter_publicPathForgotPassword_continuesChain() throws IOException, ServletException {
        setupRequestUri("/forgot-password.jsp");
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(same(request), same(response));
    }

    @Test
    public void doFilter_publicPathResetPassword_continuesChain() throws IOException, ServletException {
        setupRequestUri("/reset-password.jsp");
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(same(request), same(response));
    }

    // --- Empty context path (path = full URI) ---

    @Test
    public void doFilter_emptyContextPath_publicPath_continuesChain() throws IOException, ServletException {
        when(request.getContextPath()).thenReturn("");
        when(request.getRequestURI()).thenReturn("/login.jsp");
        filter.doFilter(request, response, chain);
        verify(request).setAttribute(eq("googleLoginEnabled"), any(Boolean.class));
        verify(chain).doFilter(same(request), same(response));
    }

    @Test
    public void doFilter_emptyContextPath_notPublicNoSession_redirectsToLogin() throws IOException, ServletException {
        when(request.getContextPath()).thenReturn("");
        when(request.getRequestURI()).thenReturn("/admin/dashboard.jsp");
        when(request.getSession(false)).thenReturn(null);
        filter.doFilter(request, response, chain);
        verify(response).sendRedirect("/login.jsp");
    }
}
