package com.oceanview.resort.controller;

import com.oceanview.resort.dto.ReportDTO;
import com.oceanview.resort.factory.ServiceFactory;
import com.oceanview.resort.service.ReportService;
import com.oceanview.resort.util.DateUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class ReportController extends HttpServlet {
    private final ReportService reportService = ServiceFactory.getInstance().getReportService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String downloadId = request.getParameter("downloadId");
        String view = request.getParameter("view");
        if (downloadId != null) {
            try {
                long id = Long.parseLong(downloadId);
                byte[] content = reportService.getContent(id);
                String format = reportService.getFormat(id);
                if (content == null || format == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                boolean inline = "1".equals(view) || "true".equalsIgnoreCase(view);
                if ("EXCEL".equalsIgnoreCase(format)) {
                    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                    response.setHeader("Content-Disposition", "attachment; filename=report-" + id + ".xlsx");
                } else {
                    response.setContentType("application/pdf");
                    String disposition = inline ? "inline" : "attachment";
                    response.setHeader("Content-Disposition", disposition + "; filename=report-" + id + ".pdf");
                }
                response.getOutputStream().write(content);
                return;
            } catch (NumberFormatException ex) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }

        List<ReportDTO> reports = reportService.findAll();
        request.setAttribute("reports", reports);
        request.getRequestDispatcher("/admin/reports.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        try {
            ReportDTO dto = new ReportDTO();
            dto.setReportType(request.getParameter("reportType"));
            dto.setFormat(request.getParameter("format"));
            String period = request.getParameter("period");
            String startInput = request.getParameter("startDate");
            String endInput = request.getParameter("endDate");

            LocalDate today = LocalDate.now();
            LocalDate startDate;
            LocalDate endDate;
            String normalizedPeriod = period == null ? "DAILY" : period.trim().toUpperCase();
            switch (normalizedPeriod) {
                case "DAILY":
                    startDate = today;
                    endDate = today;
                    break;
                case "WEEKLY":
                    startDate = today.minusDays(6);
                    endDate = today;
                    break;
                case "MONTHLY":
                    startDate = today.withDayOfMonth(1);
                    endDate = today;
                    break;
                case "CUSTOM":
                default:
                    normalizedPeriod = "CUSTOM";
                    startDate = DateUtil.parseDate(startInput);
                    endDate = DateUtil.parseDate(endInput);
                    if (startDate == null || endDate == null) {
                        throw new IllegalArgumentException("Select start and end dates for a custom report.");
                    }
                    break;
            }
            if (startDate.isAfter(endDate)) {
                throw new IllegalArgumentException("Start date cannot be after end date.");
            }

            dto.setPeriod(normalizedPeriod);
            dto.setStartDate(DateUtil.formatDate(startDate));
            dto.setEndDate(DateUtil.formatDate(endDate));
            dto.setParameters("Period=" + normalizedPeriod + "; Start=" + DateUtil.formatDate(startDate)
                    + "; End=" + DateUtil.formatDate(endDate));

            long generatedBy = getCurrentUserId(request.getSession(false));
            ReportDTO report = reportService.generate(dto, generatedBy);
            request.getSession().setAttribute("flashSuccess", "Report generated successfully.");
            request.getSession().setAttribute("lastReport", report);
        } catch (Exception ex) {
            request.getSession().setAttribute("flashError", ex.getMessage());
        }
        response.sendRedirect(request.getContextPath() + "/reports");
    }

    private long getCurrentUserId(HttpSession session) {
        if (session == null || session.getAttribute("userId") == null) {
            throw new IllegalStateException("User must be logged in to generate reports.");
        }
        return (long) session.getAttribute("userId");
    }
}
