package com.oceanview.resort.controllerTest;

import com.oceanview.resort.controller.SettingsController;
import com.oceanview.resort.service.ConfigService;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.mockito.Mockito.*;

public class SettingsControllerTest {

    private SettingsController controller;
    private ConfigService configService;

    @Before
    public void setUp() throws Exception {
        controller = new SettingsController();
        configService = mock(ConfigService.class);
        injectField("configService", configService);
    }

    private void injectField(String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = SettingsController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(controller, value);
    }

    @Test
    public void doGet_setsTaxRatePercentAndForwards() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getMethod()).thenReturn("GET");
        when(configService.getTaxRateDecimal()).thenReturn(0.10);
        when(request.getRequestDispatcher("/admin/settings.jsp")).thenReturn(dispatcher);

        controller.service(request, response);

        verify(configService).getTaxRateDecimal();
        verify(request).setAttribute("taxRatePercent", 10.0);
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void doPost_validTaxRate_savesAndRedirects() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("taxRatePercent")).thenReturn("12.5");
        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("/ctx");

        controller.service(request, response);

        verify(configService).setTaxRatePercent(12.5);
        verify(session).setAttribute("flashSuccess", "Tax rate saved.");
        verify(response).sendRedirect("/ctx/settings");
    }

    @Test
    public void doPost_invalidTaxRate_setsFlashErrorAndRedirects() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("taxRatePercent")).thenReturn("invalid");
        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("/ctx");

        controller.service(request, response);

        verify(configService, never()).setTaxRatePercent(anyDouble());
        verify(session).setAttribute("flashError", "Invalid tax rate.");
        verify(response).sendRedirect("/ctx/settings");
    }

    @Test
    public void doPost_nullTaxParam_redirectsWithoutSaving() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("taxRatePercent")).thenReturn(null);
        when(request.getContextPath()).thenReturn("/ctx");

        controller.service(request, response);

        verify(configService, never()).setTaxRatePercent(anyDouble());
        verify(response).sendRedirect("/ctx/settings");
    }
}
