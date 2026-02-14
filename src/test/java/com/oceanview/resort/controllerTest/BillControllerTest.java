package com.oceanview.resort.controllerTest;

import com.oceanview.resort.controller.BillController;
import com.oceanview.resort.dto.BillDTO;
import com.oceanview.resort.dto.DiscountDTO;
import com.oceanview.resort.dto.ReservationDTO;
import com.oceanview.resort.service.BillService;
import com.oceanview.resort.service.DiscountService;
import com.oceanview.resort.service.ReservationService;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests for BillController.
 * Focuses on controller behavior (attributes, redirects, and interaction with services).
 */
public class BillControllerTest {

    private BillController controller;
    private BillService billService;
    private ReservationService reservationService;
    private DiscountService discountService;

    @Before
    public void setUp() throws Exception {
        controller = new BillController();
        billService = mock(BillService.class);
        reservationService = mock(ReservationService.class);
        discountService = mock(DiscountService.class);

        // Inject mocks into controller using reflection
        injectField(controller, "billService", billService);
        injectField(controller, "reservationService", reservationService);
        injectField(controller, "discountService", discountService);
    }

    private void injectField(Object target, String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = BillController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }


    @Test
    public void testDoGet_WithSearchQuerySetsReservationsAndDiscountsAndForwards() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getParameter("q")).thenReturn("ABC123");
        List<ReservationDTO> reservations = Collections.singletonList(new ReservationDTO());
        List<DiscountDTO> discounts = Collections.singletonList(new DiscountDTO());
        when(reservationService.search("ABC123")).thenReturn(reservations);
        when(discountService.findActive()).thenReturn(discounts);
        when(request.getRequestDispatcher("/reservationist/billing.jsp")).thenReturn(dispatcher);

        controller.service(request, response);

        verify(reservationService).search("ABC123");
        verify(discountService).findActive();
        verify(request).setAttribute("billingReservations", reservations);
        verify(request).setAttribute("searchQuery", "ABC123");
        verify(request).setAttribute("activeDiscounts", discounts);
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void testDoGet_WithoutSearchQuerySetsOnlyDiscountsAndForwards() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getParameter("q")).thenReturn(null);
        List<DiscountDTO> discounts = Collections.singletonList(new DiscountDTO());
        when(discountService.findActive()).thenReturn(discounts);
        when(request.getRequestDispatcher("/reservationist/billing.jsp")).thenReturn(dispatcher);

        controller.service(request, response);

        verify(reservationService, never()).search(anyString());
        verify(discountService).findActive();
        verify(request).setAttribute("activeDiscounts", discounts);
        verify(dispatcher).forward(request, response);
    }

    // --- doPost tests -------------------------------------------------------

    @Test
    public void testDoPost_MissingReservationIdSetsFieldErrorAndRedirects() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("reservationId")).thenReturn(null);
        when(request.getContextPath()).thenReturn("/ctx");
        when(request.getSession()).thenReturn(session);

        controller.service(request, response);

        verify(session).setAttribute(eq("fieldErrors"), argThat(errors ->
                "Reservation ID is required.".equals(((java.util.Map<?, ?>) errors).get("reservationId"))));
        verify(response).sendRedirect("/ctx/bills");
        verifyNoInteractions(billService);
    }

    @Test
    public void testDoPost_SuccessfulBillGenerationSetsFlashSuccessAndLastBill() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("reservationId")).thenReturn("10");
        when(request.getParameter("manualDiscountPercent")).thenReturn("15");
        when(request.getParameterValues("discountIds")).thenReturn(new String[]{"1", "2"});
        when(request.getParameter("actualCheckoutDate")).thenReturn(LocalDate.now().toString());
        when(request.getContextPath()).thenReturn("/ctx");
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(5L);

        BillDTO bill = new BillDTO();
        when(billService.generate(eq(10L), eq(new BigDecimal("15")), anyList(), any(LocalDate.class), eq(5L)))
                .thenReturn(bill);

        controller.service(request, response);

        verify(billService).generate(eq(10L), eq(new BigDecimal("15")), anyList(), any(LocalDate.class), eq(5L));
        verify(session).setAttribute("flashSuccess", "Bill generated successfully.");
        verify(session).setAttribute("lastBill", bill);
        verify(response).sendRedirect("/ctx/bills");
    }

    @Test
    public void testDoPost_ReservationNotFoundSetsFlashError() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("reservationId")).thenReturn("10");
        when(request.getParameter("manualDiscountPercent")).thenReturn(null);
        when(request.getParameterValues("discountIds")).thenReturn(null);
        when(request.getParameter("actualCheckoutDate")).thenReturn(LocalDate.now().toString());
        when(request.getContextPath()).thenReturn("/ctx");
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(5L);

        when(billService.generate(anyLong(), any(), anyList(), any(LocalDate.class), anyLong())).thenReturn(null);

        controller.service(request, response);

        verify(session).setAttribute("flashError", "Reservation not found.");
        verify(response).sendRedirect("/ctx/bills");
    }

    @Test
    public void testDoPost_ExceptionFromServiceSetsFlashError() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("reservationId")).thenReturn("10");
        when(request.getParameter("manualDiscountPercent")).thenReturn("15");
        when(request.getParameterValues("discountIds")).thenReturn(new String[]{"1"});
        when(request.getParameter("actualCheckoutDate")).thenReturn(LocalDate.now().toString());
        when(request.getContextPath()).thenReturn("/ctx");
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(5L);

        when(billService.generate(anyLong(), any(), anyList(), any(LocalDate.class), anyLong()))
                .thenThrow(new RuntimeException("Boom"));

        controller.service(request, response);

        verify(session).setAttribute("flashError", "Boom");
        verify(response).sendRedirect("/ctx/bills");
    }
}

