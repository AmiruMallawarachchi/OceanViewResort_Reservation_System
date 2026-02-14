package com.oceanview.resort.controllerTest;

import com.oceanview.resort.controller.GuestController;
import com.oceanview.resort.dto.GuestDTO;
import com.oceanview.resort.service.GuestService;
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
 * Tests for GuestController with full branch coverage.
 */
public class GuestControllerTest {

    private GuestController controller;
    private GuestService guestService;
    private static final String CTX = "/ctx";

    @Before
    public void setUp() throws Exception {
        controller = new GuestController();
        guestService = mock(GuestService.class);
        injectField(controller, guestService);
    }

    private void injectField(Object target, Object value) throws Exception {
        java.lang.reflect.Field field = GuestController.class.getDeclaredField("guestService");
        field.setAccessible(true);
        field.set(target, value);
    }

    // --- doGet -----------------------------------------------------------------

    @Test
    public void doGet_withoutEditId_setsGuestsAndForwards() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        List<GuestDTO> guests = Collections.singletonList(new GuestDTO());

        when(request.getMethod()).thenReturn("GET");
        when(request.getParameter("editId")).thenReturn(null);
        when(guestService.findAll()).thenReturn(guests);
        when(request.getRequestDispatcher("/reservationist/guests.jsp")).thenReturn(dispatcher);

        controller.service(request, response);

        verify(guestService).findAll();
        verify(request).setAttribute("guests", guests);
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void doGet_withValidEditId_setsEditGuestAndGuestsAndForwards() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        GuestDTO editGuest = new GuestDTO();
        editGuest.setId(1L);

        when(request.getMethod()).thenReturn("GET");
        when(request.getParameter("editId")).thenReturn("1");
        when(guestService.findById(1L)).thenReturn(editGuest);
        when(guestService.findAll()).thenReturn(Collections.emptyList());
        when(request.getRequestDispatcher("/reservationist/guests.jsp")).thenReturn(dispatcher);

        controller.service(request, response);

        verify(guestService).findById(1L);
        verify(request).setAttribute("editGuest", editGuest);
        verify(request).setAttribute("guests", Collections.emptyList());
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void doGet_withInvalidEditId_ignoresNumberFormatAndForwards() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getParameter("editId")).thenReturn("x");
        when(guestService.findAll()).thenReturn(Collections.emptyList());
        when(request.getRequestDispatcher("/reservationist/guests.jsp")).thenReturn(dispatcher);

        controller.service(request, response);

        verify(guestService, never()).findById(anyLong());
        verify(guestService).findAll();
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

        verify(guestService).deleteGuest(5L);
        verify(session).setAttribute("flashSuccess", "Guest deleted successfully.");
        verify(response).sendRedirect(CTX + "/guests");
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
        when(request.getParameter("fullName")).thenReturn("");
        when(request.getParameter("phone")).thenReturn("123");
        when(request.getParameter("address")).thenReturn("Addr");
        when(request.getParameter("idNumber")).thenReturn("ID1");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);

        controller.service(request, response);

        verify(session).setAttribute(eq("fieldErrors"), argThat(errors ->
                "Full name is required.".equals(((java.util.Map<?, ?>) errors).get("fullName"))));
        verify(response).sendRedirect(CTX + "/guests?editId=3");
        verify(guestService, never()).updateGuest(any());
    }

    @Test
    public void doPost_update_phoneBlank_setsFieldError() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("id")).thenReturn("3");
        when(request.getParameter("fullName")).thenReturn("John");
        when(request.getParameter("phone")).thenReturn("");
        when(request.getParameter("address")).thenReturn("Addr");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);

        controller.service(request, response);

        verify(session).setAttribute(eq("fieldErrors"), argThat(errors ->
                "Phone is required.".equals(((java.util.Map<?, ?>) errors).get("phone"))));
        verify(response).sendRedirect(CTX + "/guests?editId=3");
    }

    @Test
    public void doPost_update_addressBlank_setsFieldError() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("id")).thenReturn("3");
        when(request.getParameter("fullName")).thenReturn("John");
        when(request.getParameter("phone")).thenReturn("123");
        when(request.getParameter("address")).thenReturn("");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);

        controller.service(request, response);

        verify(session).setAttribute(eq("fieldErrors"), argThat(errors ->
                "Address is required.".equals(((java.util.Map<?, ?>) errors).get("address"))));
        verify(response).sendRedirect(CTX + "/guests?editId=3");
    }

    @Test
    public void doPost_update_invalidGuestType_setsFieldError() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("id")).thenReturn("3");
        when(request.getParameter("fullName")).thenReturn("John");
        when(request.getParameter("phone")).thenReturn("123");
        when(request.getParameter("address")).thenReturn("Addr");
        when(request.getParameter("guestType")).thenReturn("INVALID_TYPE");
        when(request.getParameter("idNumber")).thenReturn("ID1");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);

        controller.service(request, response);

        verify(session).setAttribute(eq("fieldErrors"), argThat(errors ->
                "Select a valid guest type.".equals(((java.util.Map<?, ?>) errors).get("guestType"))));
        verify(response).sendRedirect(CTX + "/guests?editId=3");
    }

    @Test
    public void doPost_update_idNumberDuplicateOnOtherGuest_setsFieldError() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        GuestDTO otherGuest = new GuestDTO();
        otherGuest.setId(99L);
        otherGuest.setIdNumber("ID-123");

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("id")).thenReturn("3");
        when(request.getParameter("fullName")).thenReturn("John");
        when(request.getParameter("phone")).thenReturn("123");
        when(request.getParameter("address")).thenReturn("Addr");
        when(request.getParameter("guestType")).thenReturn("REGULAR");
        when(request.getParameter("idNumber")).thenReturn("ID-123");
        when(request.getParameter("email")).thenReturn("j@x.com");
        when(request.getParameter("idType")).thenReturn("PASSPORT");
        when(request.getParameter("nationality")).thenReturn("US");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);
        when(guestService.findByIdNumber("ID-123")).thenReturn(otherGuest);

        controller.service(request, response);

        verify(session).setAttribute(eq("fieldErrors"), argThat(errors ->
                "ID number already exists.".equals(((java.util.Map<?, ?>) errors).get("idNumber"))));
        verify(response).sendRedirect(CTX + "/guests?editId=3");
        verify(guestService, never()).updateGuest(any());
    }

    @Test
    public void doPost_update_idNumberSameGuest_noDuplicateError() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        GuestDTO sameGuest = new GuestDTO();
        sameGuest.setId(3L);
        sameGuest.setIdNumber("ID-123");

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("id")).thenReturn("3");
        when(request.getParameter("fullName")).thenReturn("John");
        when(request.getParameter("phone")).thenReturn("123");
        when(request.getParameter("address")).thenReturn("Addr");
        when(request.getParameter("guestType")).thenReturn("REGULAR");
        when(request.getParameter("idNumber")).thenReturn("ID-123");
        when(request.getParameter("email")).thenReturn("j@x.com");
        when(request.getParameter("idType")).thenReturn("PASSPORT");
        when(request.getParameter("nationality")).thenReturn("US");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);
        when(guestService.findByIdNumber("ID-123")).thenReturn(sameGuest);

        controller.service(request, response);

        verify(guestService).updateGuest(argThat(d -> d.getId() == 3L));
        verify(session).setAttribute("flashSuccess", "Guest updated successfully.");
        verify(response).sendRedirect(CTX + "/guests");
    }

    @Test
    public void doPost_update_valid_callsUpdateAndRedirects() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("id")).thenReturn("3");
        when(request.getParameter("fullName")).thenReturn("Jane Doe");
        when(request.getParameter("phone")).thenReturn("555-1234");
        when(request.getParameter("address")).thenReturn("123 Main St");
        when(request.getParameter("guestType")).thenReturn("VIP");
        when(request.getParameter("idNumber")).thenReturn("NEW-ID");
        when(request.getParameter("email")).thenReturn("jane@x.com");
        when(request.getParameter("idType")).thenReturn("PASSPORT");
        when(request.getParameter("nationality")).thenReturn("UK");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);
        when(guestService.findByIdNumber("NEW-ID")).thenReturn(null);

        controller.service(request, response);

        verify(guestService).updateGuest(argThat(d ->
                d.getId() == 3L && "Jane Doe".equals(d.getFullName()) && "VIP".equals(d.getGuestType())));
        verify(session).setAttribute("flashSuccess", "Guest updated successfully.");
        verify(response).sendRedirect(CTX + "/guests");
    }

    // --- doPost: create ---------------------------------------------------------

    @Test
    public void doPost_create_validationErrors_setsFieldErrorsAndRedirects() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("other");
        when(request.getParameter("fullName")).thenReturn("");
        when(request.getParameter("phone")).thenReturn("123");
        when(request.getParameter("address")).thenReturn("Addr");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);

        controller.service(request, response);

        verify(session).setAttribute(eq("fieldErrors"), any());
        verify(response).sendRedirect(CTX + "/guests");
        verify(guestService, never()).createGuest(any());
    }

    @Test
    public void doPost_create_idNumberDuplicate_setsFieldError() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        GuestDTO existing = new GuestDTO();
        existing.setId(1L);
        existing.setIdNumber("DUP-1");

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn(null);
        when(request.getParameter("fullName")).thenReturn("New Guest");
        when(request.getParameter("phone")).thenReturn("111");
        when(request.getParameter("address")).thenReturn("Addr");
        when(request.getParameter("guestType")).thenReturn("REGULAR");
        when(request.getParameter("idNumber")).thenReturn("DUP-1");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);
        when(guestService.findByIdNumber("DUP-1")).thenReturn(existing);

        controller.service(request, response);

        verify(session).setAttribute(eq("fieldErrors"), argThat(errors ->
                "ID number already exists.".equals(((java.util.Map<?, ?>) errors).get("idNumber"))));
        verify(response).sendRedirect(CTX + "/guests");
        verify(guestService, never()).createGuest(any());
    }

    @Test
    public void doPost_create_valid_callsCreateAndRedirects() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn(null);
        when(request.getParameter("fullName")).thenReturn("New Guest");
        when(request.getParameter("phone")).thenReturn("555-0000");
        when(request.getParameter("address")).thenReturn("456 Oak Ave");
        when(request.getParameter("guestType")).thenReturn("REGULAR");
        when(request.getParameter("idNumber")).thenReturn("ID-999");
        when(request.getParameter("email")).thenReturn("new@x.com");
        when(request.getParameter("idType")).thenReturn("ID");
        when(request.getParameter("nationality")).thenReturn("US");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);
        when(guestService.findByIdNumber("ID-999")).thenReturn(null);

        controller.service(request, response);

        verify(guestService).createGuest(argThat(d ->
                "New Guest".equals(d.getFullName()) && "REGULAR".equals(d.getGuestType())));
        verify(session).setAttribute("flashSuccess", "Guest created successfully.");
        verify(response).sendRedirect(CTX + "/guests");
    }

    @Test
    public void doPost_create_guestTypeBlank_defaultsToRegular() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn(null);
        when(request.getParameter("fullName")).thenReturn("Guest");
        when(request.getParameter("phone")).thenReturn("111");
        when(request.getParameter("address")).thenReturn("Addr");
        when(request.getParameter("guestType")).thenReturn("");
        when(request.getParameter("idNumber")).thenReturn("X1");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);
        when(guestService.findByIdNumber("X1")).thenReturn(null);

        controller.service(request, response);

        verify(guestService).createGuest(argThat(d -> "REGULAR".equals(d.getGuestType())));
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
        doThrow(new RuntimeException("DB error")).when(guestService).deleteGuest(1L);

        controller.service(request, response);

        verify(session).setAttribute("flashError", "DB error");
        verify(response).sendRedirect(CTX + "/guests");
    }
}
