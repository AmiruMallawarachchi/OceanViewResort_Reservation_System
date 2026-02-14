package com.oceanview.resort.controller;

import com.oceanview.resort.dto.DashboardSummaryDTO;
import com.oceanview.resort.dto.UserDTO;
import com.oceanview.resort.factory.ServiceFactory;
import com.oceanview.resort.service.ReportService;
import com.oceanview.resort.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller that populates the admin dashboard view with key KPIs.
 */
public class AdminDashboardController extends HttpServlet {
    private final ReportService reportService = ServiceFactory.getInstance().getReportService();
    private final UserService userService = ServiceFactory.getInstance().getUserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DashboardSummaryDTO summary = reportService.getDashboardSummary();
        request.setAttribute("dashboardSummary", summary);
        List<UserDTO> allUsers = userService.findAll();
        List<UserDTO> latestUsers = allUsers.stream().limit(10).collect(Collectors.toList());
        request.setAttribute("latestUsers", latestUsers);
        request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
    }
}

