package com.oceanview.resort.controllerTest;

import com.oceanview.resort.controller.ReservationController;
import com.oceanview.resort.dto.ReservationDTO;
import com.oceanview.resort.service.ReservationService;
import com.oceanview.resort.service.RoomService;
import com.oceanview.resort.service.RoomTypeService;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Basic tests for ReservationController:
 *  - GET list / edit
 *  - POST create / update / cancel
 *  - validation error paths.

 * These focus on controller behavior (redirects, flash / fieldErrors, and forwarding),
 * with services mocked so tests do not depend on DB or availability.
 */
public class ReservationControllerTest {

    private ReservationController reservationController;
    private ReservationService reservationService;
    private RoomTypeService roomTypeService;
    private static final String CONTEXT_PATH = "/OceanViewResort_Reservation_System";
    private static final String RESERVATIONS_JSP = "/reservationist/reservations.jsp";

    @Before
    public void setup() throws Exception {
        reservationController = new ReservationController();
        reservationService = mock(ReservationService.class);
        RoomService roomService = mock(RoomService.class);
        roomTypeService = mock(RoomTypeService.class);
        injectField("reservationService", reservationService);
        injectField("roomService", roomService);
        injectField("roomTypeService", roomTypeService);
    }

    private void injectField(String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = ReservationController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(reservationController, value);
    }

    @Test
    public void testDoGet_ForwardsToReservationsPage() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestDispatcher(RESERVATIONS_JSP)).thenReturn(dispatcher);
        when(reservationService.findAll()).thenReturn(Collections.emptyList());
        when(roomTypeService.findAll()).thenReturn(Collections.emptyList());

        reservationController.service(request, response);

        verify(request).getRequestDispatcher(RESERVATIONS_JSP);
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void testDoGet_WithEditIdLoadsEditReservation() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        ReservationDTO editReservation = new ReservationDTO();
        editReservation.setId(1L);

        when(request.getMethod()).thenReturn("GET");
        when(request.getParameter("editId")).thenReturn("1");
        when(request.getRequestDispatcher(RESERVATIONS_JSP)).thenReturn(dispatcher);
        when(reservationService.findById(1L)).thenReturn(editReservation);
        when(reservationService.findAll()).thenReturn(Collections.emptyList());
        when(roomTypeService.findAll()).thenReturn(Collections.emptyList());

        reservationController.service(request, response);

        verify(request).setAttribute(eq("editReservation"), eq(editReservation));
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void testPostCreateReservation_ValidationFailsSetsFieldErrors() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn(null); // create path
        when(request.getParameter("guestId")).thenReturn("");  // missing fields
        when(request.getParameter("roomId")).thenReturn("");
        when(request.getParameter("checkInDate")).thenReturn("");
        when(request.getParameter("checkOutDate")).thenReturn("");
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        when(request.getSession(anyBoolean())).thenReturn(session);
        when(request.getSession()).thenReturn(session);

        reservationController.service(request, response);

        verify(session).setAttribute(eq("fieldErrors"), org.mockito.ArgumentMatchers.argThat(errors -> {
            java.util.Map<?, ?> map = (java.util.Map<?, ?>) errors;
            return map.get("guestId") != null
                    && map.get("roomId") != null
                    && map.get("checkInDate") != null
                    && map.get("checkOutDate") != null;
        }));
        verify(response).sendRedirect(CONTEXT_PATH + "/reservations");
    }

    @Test
    public void testPostUpdateReservation_ValidationFailsRedirectsToEdit() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("id")).thenReturn("5");
        when(request.getParameter("guestId")).thenReturn("");
        when(request.getParameter("roomId")).thenReturn("");
        when(request.getParameter("checkInDate")).thenReturn("");
        when(request.getParameter("checkOutDate")).thenReturn("");
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        when(request.getSession(anyBoolean())).thenReturn(session);
        when(request.getSession()).thenReturn(session);

        reservationController.service(request, response);

        verify(session).setAttribute(eq("fieldErrors"), org.mockito.ArgumentMatchers.any());
        verify(response).sendRedirect(CONTEXT_PATH + "/reservations?editId=5");
    }

    @Test
    public void testPostCancelReservation_SetsFlashSuccess() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("cancel");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        when(request.getSession(anyBoolean())).thenReturn(session);
        when(request.getSession()).thenReturn(session);

        reservationController.service(request, response);

        verify(session).setAttribute(eq("flashSuccess"), eq("Reservation cancelled."));
        verify(response).sendRedirect(org.mockito.ArgumentMatchers.startsWith(CONTEXT_PATH + "/reservations"));
    }

    @Test
    public void testPostCreateReservation_WithValidFieldsSetsFlashSuccess() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn(null); // create path
        when(request.getParameter("guestId")).thenReturn("1");
        when(request.getParameter("roomId")).thenReturn("1");
        when(request.getParameter("checkInDate")).thenReturn("2026-02-12");
        when(request.getParameter("checkOutDate")).thenReturn("2026-02-13");
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        when(request.getSession(anyBoolean())).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1L);
        when(reservationService.create(org.mockito.ArgumentMatchers.any(ReservationDTO.class), anyLong())).thenReturn(new ReservationDTO());

        reservationController.service(request, response);

        verify(session).setAttribute(eq("flashSuccess"), eq("Reservation created successfully."));
        verify(response).sendRedirect(org.mockito.ArgumentMatchers.startsWith(CONTEXT_PATH + "/reservations"));
    }
}

