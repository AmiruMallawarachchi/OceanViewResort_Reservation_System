package com.oceanview.resort.controllerTest;

import com.oceanview.resort.controller.RoomTypeController;
import com.oceanview.resort.service.RoomTypeService;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests for RoomTypeController with full branch coverage.
 * RoomTypeController has only doPost (no doGet).
 */
public class RoomTypeControllerTest {

    private RoomTypeController controller;
    private RoomTypeService roomTypeService;
    private static final String CTX = "/ctx";

    @Before
    public void setUp() throws Exception {
        controller = new RoomTypeController();
        roomTypeService = mock(RoomTypeService.class);
        injectField(controller, roomTypeService);
    }

    private void injectField(Object target, Object value) throws Exception {
        java.lang.reflect.Field field = RoomTypeController.class.getDeclaredField("roomTypeService");
        field.setAccessible(true);
        field.set(target, value);
    }

    // --- doPost: delete ---------------------------------------------------------

    @Test
    public void doPost_delete_setsFlashSuccessAndRedirects() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("id")).thenReturn("5");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);

        controller.service(request, response);

        verify(roomTypeService).delete(5L);
        verify(session).setAttribute("flashSuccess", "Room type deleted successfully.");
        verify(response).sendRedirect(CTX + "/rooms");
    }

    // --- doPost: update ---------------------------------------------------------

    @Test
    public void doPost_update_validationErrors_setsFieldErrorsAndRedirectsToEditType() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("id")).thenReturn("3");
        when(request.getParameter("typeName")).thenReturn("");
        when(request.getParameter("ratePerNight")).thenReturn("100");
        when(request.getParameter("maxOccupancy")).thenReturn("2");
        when(request.getParameter("amenities")).thenReturn("WiFi");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);

        controller.service(request, response);

        verify(session).setAttribute(eq("fieldErrors"), argThat(errors ->
                "Type name is required.".equals(((java.util.Map<?, ?>) errors).get("typeName"))));
        verify(response).sendRedirect(CTX + "/rooms?editTypeId=3");
        verify(roomTypeService, never()).update(any());
    }

    @Test
    public void doPost_update_ratePerNightBlank_setsFieldError() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("id")).thenReturn("3");
        when(request.getParameter("typeName")).thenReturn("Deluxe");
        when(request.getParameter("ratePerNight")).thenReturn("");
        when(request.getParameter("maxOccupancy")).thenReturn("3");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);

        controller.service(request, response);

        verify(session).setAttribute(eq("fieldErrors"), argThat(errors ->
                "Rate per night is required.".equals(((java.util.Map<?, ?>) errors).get("ratePerNight"))));
        verify(response).sendRedirect(CTX + "/rooms?editTypeId=3");
    }

    @Test
    public void doPost_update_maxOccupancyBlank_setsFieldError() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("id")).thenReturn("3");
        when(request.getParameter("typeName")).thenReturn("Suite");
        when(request.getParameter("ratePerNight")).thenReturn("200");
        when(request.getParameter("maxOccupancy")).thenReturn("");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);

        controller.service(request, response);

        verify(session).setAttribute(eq("fieldErrors"), argThat(errors ->
                "Max occupancy is required.".equals(((java.util.Map<?, ?>) errors).get("maxOccupancy"))));
        verify(response).sendRedirect(CTX + "/rooms?editTypeId=3");
    }

    @Test
    public void doPost_update_valid_callsUpdateAndRedirects() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("id")).thenReturn("3");
        when(request.getParameter("typeName")).thenReturn("Deluxe");
        when(request.getParameter("ratePerNight")).thenReturn("150.00");
        when(request.getParameter("amenities")).thenReturn("Sea view, Mini bar");
        when(request.getParameter("maxOccupancy")).thenReturn("4");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);

        controller.service(request, response);

        verify(roomTypeService).update(argThat(d ->
                d.getId() == 3L && "Deluxe".equals(d.getTypeName()) && "150.00".equals(d.getRatePerNight())
                        && d.getMaxOccupancy() == 4 && d.isActive()));
        verify(session).setAttribute("flashSuccess", "Room type updated successfully.");
        verify(response).sendRedirect(CTX + "/rooms");
    }

    // --- doPost: create ---------------------------------------------------------

    @Test
    public void doPost_create_validationErrors_setsFieldErrorsAndRedirects() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("other");
        when(request.getParameter("typeName")).thenReturn("");
        when(request.getParameter("ratePerNight")).thenReturn("99");
        when(request.getParameter("maxOccupancy")).thenReturn("2");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);

        controller.service(request, response);

        verify(session).setAttribute(eq("fieldErrors"), any());
        verify(response).sendRedirect(CTX + "/rooms");
        verify(roomTypeService, never()).create(any());
    }

    @Test
    public void doPost_create_valid_callsCreateAndRedirects() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn(null);
        when(request.getParameter("typeName")).thenReturn("Standard");
        when(request.getParameter("ratePerNight")).thenReturn("89.99");
        when(request.getParameter("amenities")).thenReturn("WiFi, TV");
        when(request.getParameter("maxOccupancy")).thenReturn("2");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);

        controller.service(request, response);

        verify(roomTypeService).create(argThat(d ->
                "Standard".equals(d.getTypeName()) && "89.99".equals(d.getRatePerNight())
                        && d.getMaxOccupancy() == 2 && d.isActive()));
        verify(session).setAttribute("flashSuccess", "Room type created successfully.");
        verify(response).sendRedirect(CTX + "/rooms");
    }

    // --- doPost: exception ------------------------------------------------------

    @Test
    public void doPost_exception_setsFlashErrorAndRedirects() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);
        doThrow(new RuntimeException("DB error")).when(roomTypeService).delete(1L);

        controller.service(request, response);

        verify(session).setAttribute("flashError", "DB error");
        verify(response).sendRedirect(CTX + "/rooms");
    }
}
