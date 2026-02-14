package com.oceanview.resort.controllerTest;

import com.oceanview.resort.controller.DiscountController;
import com.oceanview.resort.dto.DiscountDTO;
import com.oceanview.resort.service.DiscountService;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests for DiscountController with full branch coverage.
 */
public class DiscountControllerTest {

    private DiscountController controller;
    private DiscountService discountService;
    private static final String CTX = "/ctx";

    @Before
    public void setUp() throws Exception {
        controller = new DiscountController();
        discountService = mock(DiscountService.class);
        injectField(controller, discountService);
    }

    private void injectField(Object target, Object value) throws Exception {
        java.lang.reflect.Field field = DiscountController.class.getDeclaredField("discountService");
        field.setAccessible(true);
        field.set(target, value);
    }

    // --- doGet -----------------------------------------------------------------

    @Test
    public void doGet_withoutEditId_setsDiscountsAndForwards() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getParameter("editId")).thenReturn(null);
        List<DiscountDTO> discounts = Collections.singletonList(new DiscountDTO());
        when(discountService.findAll()).thenReturn(discounts);
        when(request.getRequestDispatcher("/admin/discounts.jsp")).thenReturn(dispatcher);

        controller.service(request, response);

        verify(discountService).findAll();
        verify(request).setAttribute("discounts", discounts);
        verify(dispatcher).forward(request, response);

    }

    @Test
    public void doGet_withValidEditId_setsEditDiscountAndDiscountsAndForwards() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        DiscountDTO editDiscount = new DiscountDTO();
        editDiscount.setId(1L);

        when(request.getMethod()).thenReturn("GET");
        when(request.getParameter("editId")).thenReturn("1");
        when(discountService.findById(1L)).thenReturn(editDiscount);
        when(discountService.findAll()).thenReturn(Collections.emptyList());
        when(request.getRequestDispatcher("/admin/discounts.jsp")).thenReturn(dispatcher);

        controller.service(request, response);

        verify(discountService).findById(1L);
        verify(request).setAttribute("editDiscount", editDiscount);
        verify(request).setAttribute("discounts", Collections.emptyList());
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void doGet_withInvalidEditId_ignoresNumberFormatAndForwards() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getParameter("editId")).thenReturn("abc");
        when(discountService.findAll()).thenReturn(Collections.emptyList());
        when(request.getRequestDispatcher("/admin/discounts.jsp")).thenReturn(dispatcher);

        controller.service(request, response);

        verify(discountService, never()).findById(anyLong());
        verify(discountService).findAll();
        verify(dispatcher).forward(request, response);
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

        verify(discountService).delete(5L);
        verify(session).setAttribute("flashSuccess", "Discount deleted successfully.");
        verify(response).sendRedirect(CTX + "/discounts");
    }

    // --- doPost: toggle ---------------------------------------------------------

    @Test
    public void doPost_toggle_existingNotNull_updatesAndSetsFlashSuccess() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        DiscountDTO existing = new DiscountDTO();
        existing.setId(2L);
        existing.setActive(true);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("toggle");
        when(request.getParameter("id")).thenReturn("2");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);
        when(discountService.findById(2L)).thenReturn(existing);

        controller.service(request, response);

        verify(discountService).findById(2L);
        verify(discountService).update(argThat(d -> d.getId() == 2L && !d.isActive()));
        verify(session).setAttribute("flashSuccess", "Discount status updated.");
        verify(response).sendRedirect(CTX + "/discounts");
    }

    @Test
    public void doPost_toggle_existingNull_redirectsWithoutUpdate() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("toggle");
        when(request.getParameter("id")).thenReturn("99");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);
        when(discountService.findById(99L)).thenReturn(null);

        controller.service(request, response);

        verify(discountService).findById(99L);
        verify(discountService, never()).update(any());
        verify(response).sendRedirect(CTX + "/discounts");
    }

    // --- doPost: update ---------------------------------------------------------

    @Test
    public void doPost_update_validationErrors_setsFieldErrorsAndRedirectsToEdit() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("id")).thenReturn("3");
        when(request.getParameter("name")).thenReturn("");
        when(request.getParameter("discountType")).thenReturn("PERCENT");
        when(request.getParameter("percent")).thenReturn("10");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);

        controller.service(request, response);

        verify(session).setAttribute(eq("fieldErrors"), argThat(errors ->
                "Discount name is required.".equals(((java.util.Map<?, ?>) errors).get("name"))));
        verify(response).sendRedirect(CTX + "/discounts?editId=3");
        verify(discountService, never()).update(any());
    }

    @Test
    public void doPost_update_discountTypeBlank_setsFieldError() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("id")).thenReturn("3");
        when(request.getParameter("name")).thenReturn("Summer");
        when(request.getParameter("discountType")).thenReturn("");
        when(request.getParameter("percent")).thenReturn("10");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);

        controller.service(request, response);

        verify(session).setAttribute(eq("fieldErrors"), argThat(errors ->
                "Discount type is required.".equals(((java.util.Map<?, ?>) errors).get("discountType"))));
        verify(response).sendRedirect(CTX + "/discounts?editId=3");
    }

    @Test
    public void doPost_update_guestTypeDiscountWithoutGuestType_setsFieldError() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("id")).thenReturn("3");
        when(request.getParameter("name")).thenReturn("VIP");
        when(request.getParameter("discountType")).thenReturn("GUEST_TYPE");
        when(request.getParameter("guestType")).thenReturn("");
        when(request.getParameter("percent")).thenReturn("15");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);

        controller.service(request, response);

        verify(session).setAttribute(eq("fieldErrors"), argThat(errors ->
                "Guest type is required for guest discounts.".equals(((java.util.Map<?, ?>) errors).get("guestType"))));
        verify(response).sendRedirect(CTX + "/discounts?editId=3");
    }

    @Test
    public void doPost_update_percentInvalidNumber_setsFieldError() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("id")).thenReturn("3");
        when(request.getParameter("name")).thenReturn("Test");
        when(request.getParameter("discountType")).thenReturn("PERCENT");
        when(request.getParameter("percent")).thenReturn("abc");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);

        controller.service(request, response);

        verify(session).setAttribute(eq("fieldErrors"), argThat(errors ->
                "Discount percent must be a valid number.".equals(((java.util.Map<?, ?>) errors).get("percent"))));
        verify(response).sendRedirect(CTX + "/discounts?editId=3");
    }

    @Test
    public void doPost_update_percentOutOfRange_setsFieldError() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("id")).thenReturn("3");
        when(request.getParameter("name")).thenReturn("Test");
        when(request.getParameter("discountType")).thenReturn("PERCENT");
        when(request.getParameter("percent")).thenReturn("150");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);

        controller.service(request, response);

        verify(session).setAttribute(eq("fieldErrors"), argThat(errors ->
                "Discount percent must be between 0 and 100.".equals(((java.util.Map<?, ?>) errors).get("percent"))));
        verify(response).sendRedirect(CTX + "/discounts?editId=3");
    }

    @Test
    public void doPost_update_valid_callsUpdateAndRedirects() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("id")).thenReturn("3");
        when(request.getParameter("name")).thenReturn("Winter");
        when(request.getParameter("discountType")).thenReturn("PERCENT");
        when(request.getParameter("percent")).thenReturn("20");
        when(request.getParameter("description")).thenReturn("Winter sale");
        when(request.getParameter("active")).thenReturn("true");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);

        controller.service(request, response);

        verify(discountService).update(argThat(d ->
                d.getId() == 3L && "Winter".equals(d.getName()) && "20".equals(d.getPercent()) && d.isActive()));
        verify(session).setAttribute("flashSuccess", "Discount updated successfully.");
        verify(response).sendRedirect(CTX + "/discounts");
    }

    @Test
    public void doPost_update_validWithGuestType_setsGuestTypeOnDto() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("id")).thenReturn("4");
        when(request.getParameter("name")).thenReturn("VIP");
        when(request.getParameter("discountType")).thenReturn("GUEST_TYPE");
        when(request.getParameter("guestType")).thenReturn("VIP");
        when(request.getParameter("percent")).thenReturn("25");
        when(request.getParameter("active")).thenReturn("false");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);

        controller.service(request, response);

        verify(discountService).update(argThat(d ->
                "GUEST_TYPE".equals(d.getDiscountType()) && "VIP".equals(d.getGuestType())));
        verify(session).setAttribute("flashSuccess", "Discount updated successfully.");
    }

    // --- doPost: create ---------------------------------------------------------

    @Test
    public void doPost_create_validationErrors_setsFieldErrorsAndRedirects() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("other");
        when(request.getParameter("name")).thenReturn("");
        when(request.getParameter("discountType")).thenReturn("PERCENT");
        when(request.getParameter("percent")).thenReturn("5");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);

        controller.service(request, response);

        verify(session).setAttribute(eq("fieldErrors"), any());
        verify(response).sendRedirect(CTX + "/discounts");
        verify(discountService, never()).create(any());
    }

    @Test
    public void doPost_create_percentBlank_setsFieldError() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn(null);
        when(request.getParameter("name")).thenReturn("New");
        when(request.getParameter("discountType")).thenReturn("PERCENT");
        when(request.getParameter("percent")).thenReturn(null);
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);

        controller.service(request, response);

        verify(session).setAttribute(eq("fieldErrors"), argThat(errors ->
                "Discount percent is required.".equals(((java.util.Map<?, ?>) errors).get("percent"))));
        verify(response).sendRedirect(CTX + "/discounts");
    }

    @Test
    public void doPost_create_valid_callsCreateAndRedirects() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn(null);
        when(request.getParameter("name")).thenReturn("New Discount");
        when(request.getParameter("discountType")).thenReturn("PERCENT");
        when(request.getParameter("percent")).thenReturn("10");
        when(request.getParameter("description")).thenReturn("Desc");
        when(request.getParameter("active")).thenReturn("true");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);

        controller.service(request, response);

        verify(discountService).create(argThat(d ->
                "New Discount".equals(d.getName()) && "10".equals(d.getPercent()) && d.isActive()));
        verify(session).setAttribute("flashSuccess", "Discount created successfully.");
        verify(response).sendRedirect(CTX + "/discounts");
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
        doThrow(new RuntimeException("DB error")).when(discountService).delete(1L);

        controller.service(request, response);

        verify(session).setAttribute("flashError", "DB error");
        verify(response).sendRedirect(CTX + "/discounts");
    }
}
