package com.oceanview.resort.controllerTest;

import com.oceanview.resort.controller.ReportController;
import com.oceanview.resort.dto.ReportDTO;
import com.oceanview.resort.service.ReportService;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests for ReportController with full branch coverage.
 */
public class ReportControllerTest {

    private ReportController controller;
    private ReportService reportService;
    private static final String CTX = "/ctx";

    @Before
    public void setUp() throws Exception {
        controller = new ReportController();
        reportService = mock(ReportService.class);
        injectField(ReportController.class, controller, "reportService", reportService);
    }

    private void injectField(Class<?> clazz, Object target, String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    // --- doGet: list (no downloadId) -------------------------------------------

    @Test
    public void doGet_withoutDownloadId_setsReportsAndForwards() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        List<ReportDTO> reports = Collections.singletonList(new ReportDTO());

        when(request.getMethod()).thenReturn("GET");
        when(request.getParameter("downloadId")).thenReturn(null);
        when(reportService.findAll()).thenReturn(reports);
        when(request.getRequestDispatcher("/admin/reports.jsp")).thenReturn(dispatcher);

        controller.service(request, response);

        verify(reportService).findAll();
        verify(request).setAttribute("reports", reports);
        verify(dispatcher).forward(request, response);
    }

    // --- doGet: download --------------------------------------------------------

    @Test
    public void doGet_downloadId_validExcel_setsContentTypeAndWrites() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletOutputStream out = mock(ServletOutputStream.class);
        byte[] content = new byte[]{1, 2, 3};

        when(request.getMethod()).thenReturn("GET");
        when(request.getParameter("downloadId")).thenReturn("10");
        when(request.getParameter("view")).thenReturn(null);
        when(reportService.getContent(10L)).thenReturn(content);
        when(reportService.getFormat(10L)).thenReturn("EXCEL");
        when(response.getOutputStream()).thenReturn(out);

        controller.service(request, response);

        verify(response).setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        verify(response).setHeader("Content-Disposition", "attachment; filename=report-10.xlsx");
        verify(out).write(content);
        verify(reportService, never()).findAll();
    }

    @Test
    public void doGet_downloadId_validPdf_inlineWhenViewIs1() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletOutputStream out = mock(ServletOutputStream.class);
        byte[] content = new byte[]{1, 2};

        when(request.getMethod()).thenReturn("GET");
        when(request.getParameter("downloadId")).thenReturn("5");
        when(request.getParameter("view")).thenReturn("1");
        when(reportService.getContent(5L)).thenReturn(content);
        when(reportService.getFormat(5L)).thenReturn("PDF");
        when(response.getOutputStream()).thenReturn(out);

        controller.service(request, response);

        verify(response).setContentType("application/pdf");
        verify(response).setHeader("Content-Disposition", "inline; filename=report-5.pdf");
        verify(out).write(content);
    }

    @Test
    public void doGet_downloadId_validPdf_attachmentWhenViewNotSet() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletOutputStream out = mock(ServletOutputStream.class);
        byte[] content = new byte[]{1};

        when(request.getMethod()).thenReturn("GET");
        when(request.getParameter("downloadId")).thenReturn("7");
        when(request.getParameter("view")).thenReturn("false");
        when(reportService.getContent(7L)).thenReturn(content);
        when(reportService.getFormat(7L)).thenReturn("pdf");
        when(response.getOutputStream()).thenReturn(out);

        controller.service(request, response);

        verify(response).setContentType("application/pdf");
        verify(response).setHeader("Content-Disposition", "attachment; filename=report-7.pdf");
        verify(out).write(content);
    }

    @Test
    public void doGet_downloadId_contentNull_sends404() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getParameter("downloadId")).thenReturn("1");
        when(reportService.getContent(1L)).thenReturn(null);
        when(reportService.getFormat(1L)).thenReturn("PDF");

        controller.service(request, response);

        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);
        verify(response, never()).getOutputStream();
    }

    @Test
    public void doGet_downloadId_formatNull_sends404() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getParameter("downloadId")).thenReturn("1");
        when(reportService.getContent(1L)).thenReturn(new byte[0]);
        when(reportService.getFormat(1L)).thenReturn(null);

        controller.service(request, response);

        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    public void doGet_downloadId_invalidNumber_sends400() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getParameter("downloadId")).thenReturn("abc");

        controller.service(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
        verify(reportService, never()).getContent(anyLong());
    }

    // --- doPost: generate -------------------------------------------------------

    @Test
    public void doPost_periodDaily_setsDatesAndCallsGenerate() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        ReportDTO saved = new ReportDTO();
        saved.setId(1L);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("reportType")).thenReturn("OCCUPANCY");
        when(request.getParameter("format")).thenReturn("PDF");
        when(request.getParameter("period")).thenReturn("DAILY");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(10L);
        when(reportService.generate(any(ReportDTO.class), eq(10L))).thenReturn(saved);

        controller.service(request, response);

        verify(reportService).generate(argThat(dto ->
                "DAILY".equals(dto.getPeriod()) && dto.getStartDate() != null && dto.getEndDate() != null), eq(10L));
        verify(session).setAttribute("flashSuccess", "Report generated successfully.");
        verify(session).setAttribute("lastReport", saved);
        verify(response).sendRedirect(CTX + "/reports");
    }

    @Test
    public void doPost_periodWeekly_setsDateRange() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("reportType")).thenReturn("REVENUE");
        when(request.getParameter("format")).thenReturn("EXCEL");
        when(request.getParameter("period")).thenReturn("WEEKLY");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(5L);
        when(reportService.generate(any(ReportDTO.class), eq(5L))).thenReturn(new ReportDTO());

        controller.service(request, response);

        verify(reportService).generate(argThat(dto -> "WEEKLY".equals(dto.getPeriod())), eq(5L));
    }

    @Test
    public void doPost_periodMonthly_setsDateRange() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("reportType")).thenReturn("REVENUE");
        when(request.getParameter("format")).thenReturn("PDF");
        when(request.getParameter("period")).thenReturn("MONTHLY");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(5L);
        when(reportService.generate(any(ReportDTO.class), eq(5L))).thenReturn(new ReportDTO());

        controller.service(request, response);

        verify(reportService).generate(argThat(dto -> "MONTHLY".equals(dto.getPeriod())), eq(5L));
    }

    @Test
    public void doPost_periodNull_defaultsToDaily() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("reportType")).thenReturn("OCCUPANCY");
        when(request.getParameter("format")).thenReturn("PDF");
        when(request.getParameter("period")).thenReturn(null);
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1L);
        when(reportService.generate(any(ReportDTO.class), eq(1L))).thenReturn(new ReportDTO());

        controller.service(request, response);

        verify(reportService).generate(argThat(dto -> "DAILY".equals(dto.getPeriod())), eq(1L));
    }

    @Test
    public void doPost_periodCustom_validDates_callsGenerate() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        String start = "2026-01-01";
        String end = "2026-01-31";

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("reportType")).thenReturn("REVENUE");
        when(request.getParameter("format")).thenReturn("PDF");
        when(request.getParameter("period")).thenReturn("CUSTOM");
        when(request.getParameter("startDate")).thenReturn(start);
        when(request.getParameter("endDate")).thenReturn(end);
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(2L);
        when(reportService.generate(any(ReportDTO.class), eq(2L))).thenReturn(new ReportDTO());

        controller.service(request, response);

        verify(reportService).generate(argThat(dto ->
                "CUSTOM".equals(dto.getPeriod()) && start.equals(dto.getStartDate()) && end.equals(dto.getEndDate())), eq(2L));
        verify(session).setAttribute("flashSuccess", "Report generated successfully.");
    }

    @Test
    public void doPost_periodCustom_nullDates_setsFlashError() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("reportType")).thenReturn("REVENUE");
        when(request.getParameter("format")).thenReturn("PDF");
        when(request.getParameter("period")).thenReturn("CUSTOM");
        when(request.getParameter("startDate")).thenReturn(null);
        when(request.getParameter("endDate")).thenReturn(null);
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1L);

        controller.service(request, response);

        verify(session).setAttribute("flashError", "Select start and end dates for a custom report.");
        verify(reportService, never()).generate(any(), anyLong());
        verify(response).sendRedirect(CTX + "/reports");
    }

    @Test
    public void doPost_periodCustom_startAfterEnd_setsFlashError() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("reportType")).thenReturn("REVENUE");
        when(request.getParameter("format")).thenReturn("PDF");
        when(request.getParameter("period")).thenReturn("CUSTOM");
        when(request.getParameter("startDate")).thenReturn("2026-02-01");
        when(request.getParameter("endDate")).thenReturn("2026-01-01");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1L);

        controller.service(request, response);

        verify(session).setAttribute("flashError", "Start date cannot be after end date.");
        verify(reportService, never()).generate(any(), anyLong());
        verify(response).sendRedirect(CTX + "/reports");
    }

    @Test
    public void doPost_unknownPeriod_treatedAsCustom() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("reportType")).thenReturn("REVENUE");
        when(request.getParameter("format")).thenReturn("PDF");
        when(request.getParameter("period")).thenReturn("unknown");
        when(request.getParameter("startDate")).thenReturn("2026-01-01");
        when(request.getParameter("endDate")).thenReturn("2026-01-02");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1L);
        when(reportService.generate(any(ReportDTO.class), eq(1L))).thenReturn(new ReportDTO());

        controller.service(request, response);

        verify(reportService).generate(argThat(dto -> "CUSTOM".equals(dto.getPeriod())), eq(1L));
    }

    @Test
    public void doPost_sessionNull_setsFlashError() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("reportType")).thenReturn("REVENUE");
        when(request.getParameter("format")).thenReturn("PDF");
        when(request.getParameter("period")).thenReturn("DAILY");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession(false)).thenReturn(null);
        when(request.getSession()).thenReturn(session);

        controller.service(request, response);

        verify(session).setAttribute("flashError", "User must be logged in to generate reports.");
        verify(reportService, never()).generate(any(), anyLong());
    }

    @Test
    public void doPost_userIdNull_setsFlashError() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("reportType")).thenReturn("REVENUE");
        when(request.getParameter("format")).thenReturn("PDF");
        when(request.getParameter("period")).thenReturn("DAILY");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(null);

        controller.service(request, response);

        verify(session).setAttribute("flashError", "User must be logged in to generate reports.");
        verify(reportService, never()).generate(any(), anyLong());
    }

    @Test
    public void doPost_generateThrows_setsFlashError() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("reportType")).thenReturn("REVENUE");
        when(request.getParameter("format")).thenReturn("PDF");
        when(request.getParameter("period")).thenReturn("DAILY");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1L);
        when(reportService.generate(any(ReportDTO.class), eq(1L))).thenThrow(new RuntimeException("Export failed"));

        controller.service(request, response);

        verify(session).setAttribute("flashError", "Export failed");
        verify(response).sendRedirect(CTX + "/reports");
    }
}
