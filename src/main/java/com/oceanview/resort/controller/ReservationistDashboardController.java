package com.oceanview.resort.controller;

import com.oceanview.resort.dto.DashboardSummaryDTO;
import com.oceanview.resort.dto.ReservationDTO;
import com.oceanview.resort.factory.ServiceFactory;
import com.oceanview.resort.service.ReportService;
import com.oceanview.resort.service.ReservationService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Serves the reservationist (front desk) dashboard with guests and arrivals data.
 */
public class ReservationistDashboardController extends HttpServlet {
    private final ReservationService reservationService = ServiceFactory.getInstance().getReservationService();
    private final ReportService reportService = ServiceFactory.getInstance().getReportService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<ReservationDTO> all = reservationService.findAll();
        String today = LocalDate.now().toString();
        List<ReservationDTO> guestsAndArrivals = new ArrayList<>();
        for (ReservationDTO r : all) {
            if (r.getStatus() == null || "CANCELLED".equalsIgnoreCase(r.getStatus())) continue;
            String checkIn = r.getCheckInDate();
            String checkOut = r.getCheckOutDate();
            String status = r.getStatus();
            boolean arrivalToday = checkIn != null && checkIn.startsWith(today);
            boolean departureToday = checkOut != null && checkOut.startsWith(today);
            boolean inHouse = "CHECKED_IN".equalsIgnoreCase(status);
            if (arrivalToday || inHouse || departureToday) {
                guestsAndArrivals.add(r);
            }
        }
        request.setAttribute("guestsAndArrivals", guestsAndArrivals);
        DashboardSummaryDTO summary = reportService.getDashboardSummary();
        request.setAttribute("dashboardSummary", summary);
        request.getRequestDispatcher("/reservationist/dashboard.jsp").forward(request, response);
    }
}
