package com.oceanview.resort.controllerTest;

import com.oceanview.resort.controller.AdminDashboardController;
import com.oceanview.resort.dto.DashboardSummaryDTO;
import com.oceanview.resort.service.ReportService;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests for AdminDashboardController.
 * Focuses on ensuring that the controller:
 * - Calls ReportService.getDashboardSummary()
 * - Stores the summary as a request attribute
 * - Forwards to /admin/dashboard.jsp
 */
public class AdminDashboardControllerTest {

    private AdminDashboardController controller;
    private ReportService reportService;

    @Before
    public void setUp() throws Exception {
        controller = new AdminDashboardController();
        reportService = mock(ReportService.class);

        // Inject mocked ReportService into controller
        java.lang.reflect.Field field = AdminDashboardController.class.getDeclaredField("reportService");
        field.setAccessible(true);
        field.set(controller, reportService);
    }

    @Test
    public void testDoGet_PopulatesDashboardAndForwards() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getMethod()).thenReturn("GET");

        DashboardSummaryDTO summary = new DashboardSummaryDTO();
        when(reportService.getDashboardSummary()).thenReturn(summary);
        when(request.getRequestDispatcher("/admin/dashboard.jsp")).thenReturn(dispatcher);

        // Use HttpServlet.service so that the protected doGet is invoked
        controller.service(request, response);

        // Ensure service is called
        verify(reportService).getDashboardSummary();
        // Attribute set
        verify(request).setAttribute(eq("dashboardSummary"), eq(summary));
        // Forward to JSP
        verify(dispatcher).forward(request, response);
        // Ensure no redirect happened
        verify(response, never()).sendRedirect(anyString());
    }
}

