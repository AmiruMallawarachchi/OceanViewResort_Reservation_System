package com.oceanview.resort.controller;

import com.oceanview.resort.dto.DashboardSummaryDTO;
import com.oceanview.resort.factory.ServiceFactory;
import com.oceanview.resort.service.ReportService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controller that populates the admin dashboard view with key KPIs.
 */
public class AdminDashboardController extends HttpServlet {
    private final ReportService reportService = ServiceFactory.getInstance().getReportService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DashboardSummaryDTO summary = reportService.getDashboardSummary();
        request.setAttribute("dashboardSummary", summary);
        request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
    }
}

