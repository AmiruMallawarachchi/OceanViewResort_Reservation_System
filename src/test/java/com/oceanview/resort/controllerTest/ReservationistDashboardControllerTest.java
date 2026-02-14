package com.oceanview.resort.controllerTest;

import com.oceanview.resort.controller.ReservationistDashboardController;
import com.oceanview.resort.dto.DashboardSummaryDTO;
import com.oceanview.resort.dto.ReservationDTO;
import com.oceanview.resort.service.ReportService;
import com.oceanview.resort.service.ReservationService;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ReservationistDashboardControllerTest {

    private ReservationistDashboardController controller;
    private ReportService reportService;
    private ReservationService reservationService;

    @Before
    public void setUp() throws Exception {
        controller = new ReservationistDashboardController();
        reportService = mock(ReportService.class);
        reservationService = mock(ReservationService.class);
        injectField("reportService", reportService);
        injectField("reservationService", reservationService);
    }

    private void injectField(String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = ReservationistDashboardController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(controller, value);
    }

    @Test
    public void doGet_setsGuestsAndArrivalsAndSummaryAndForwards() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        ReservationDTO r1 = new ReservationDTO();
        r1.setStatus("CHECKED_IN");
        r1.setCheckInDate("2026-02-14");
        r1.setCheckOutDate("2026-02-16");
        List<ReservationDTO> all = Arrays.asList(r1);
        DashboardSummaryDTO summary = new DashboardSummaryDTO();

        when(request.getMethod()).thenReturn("GET");
        when(reservationService.findAll()).thenReturn(all);
        when(reportService.getDashboardSummary()).thenReturn(summary);
        when(request.getRequestDispatcher("/reservationist/dashboard.jsp")).thenReturn(dispatcher);

        controller.service(request, response);

        verify(reservationService).findAll();
        verify(reportService).getDashboardSummary();
        verify(request).setAttribute("dashboardSummary", summary);
        verify(request).setAttribute(eq("guestsAndArrivals"), anyList());
        verify(dispatcher).forward(request, response);
    }
}
