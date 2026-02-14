package com.oceanview.resort.controller;

import com.oceanview.resort.factory.ServiceFactory;
import com.oceanview.resort.service.ConfigService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SettingsController extends HttpServlet {
    private final ConfigService configService = ServiceFactory.getInstance().getConfigService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        double taxPercent = configService.getTaxRateDecimal() * 100;
        request.setAttribute("taxRatePercent", taxPercent);
        request.getRequestDispatcher("/admin/settings.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        String taxStr = request.getParameter("taxRatePercent");
        if (taxStr != null && !taxStr.isBlank()) {
            try {
                double percent = Double.parseDouble(taxStr.trim());
                configService.setTaxRatePercent(percent);
                request.getSession().setAttribute("flashSuccess", "Tax rate saved.");
            } catch (NumberFormatException e) {
                request.getSession().setAttribute("flashError", "Invalid tax rate.");
            }
        }
        response.sendRedirect(request.getContextPath() + "/settings");
    }
}
