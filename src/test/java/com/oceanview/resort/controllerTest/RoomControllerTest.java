package com.oceanview.resort.controllerTest;

import com.oceanview.resort.controller.RoomController;
import com.oceanview.resort.dto.RoomDTO;
import com.oceanview.resort.dto.RoomTypeDTO;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests for RoomController with full branch coverage.
 */
public class RoomControllerTest {

    private RoomController controller;
    private RoomService roomService;
    private RoomTypeService roomTypeService;
    private static final String CTX = "/ctx";

    @Before
    public void setUp() throws Exception {
        controller = new RoomController();
        roomService = mock(RoomService.class);
        roomTypeService = mock(RoomTypeService.class);
        injectField(controller, "roomService", roomService);
        injectField(controller, "roomTypeService", roomTypeService);
    }

    private void injectField(Object target, String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = RoomController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    // --- doGet -----------------------------------------------------------------

    @Test
    public void doGet_withoutEditId_setsRoomsAndRoomTypesAndForwards() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        List<RoomDTO> rooms = Collections.singletonList(new RoomDTO());
        List<RoomTypeDTO> roomTypes = Collections.singletonList(new RoomTypeDTO());

        when(request.getMethod()).thenReturn("GET");
        when(request.getParameter("editId")).thenReturn(null);
        when(request.getParameter("editTypeId")).thenReturn(null);
        when(roomService.findAll()).thenReturn(rooms);
        when(roomTypeService.findAll()).thenReturn(roomTypes);
        when(request.getRequestDispatcher("/admin/rooms.jsp")).thenReturn(dispatcher);

        controller.service(request, response);

        verify(roomService).findAll();
        verify(roomTypeService).findAll();
        verify(request).setAttribute("rooms", rooms);
        verify(request).setAttribute("roomTypes", roomTypes);
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void doGet_withValidEditId_setsEditRoomAndForwards() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        RoomDTO editRoom = new RoomDTO();
        editRoom.setId(1L);

        when(request.getMethod()).thenReturn("GET");
        when(request.getParameter("editId")).thenReturn("1");
        when(request.getParameter("editTypeId")).thenReturn(null);
        when(roomService.findById(1L)).thenReturn(editRoom);
        when(roomService.findAll()).thenReturn(Collections.emptyList());
        when(roomTypeService.findAll()).thenReturn(Collections.emptyList());
        when(request.getRequestDispatcher("/admin/rooms.jsp")).thenReturn(dispatcher);

        controller.service(request, response);

        verify(roomService).findById(1L);
        verify(request).setAttribute("editRoom", editRoom);
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void doGet_withInvalidEditId_ignoresNumberFormat() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getParameter("editId")).thenReturn("x");
        when(request.getParameter("editTypeId")).thenReturn(null);
        when(roomService.findAll()).thenReturn(Collections.emptyList());
        when(roomTypeService.findAll()).thenReturn(Collections.emptyList());
        when(request.getRequestDispatcher("/admin/rooms.jsp")).thenReturn(dispatcher);

        controller.service(request, response);

        verify(roomService, never()).findById(anyLong());
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void doGet_withValidEditTypeId_setsEditRoomTypeAndForwards() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        RoomTypeDTO editRoomType = new RoomTypeDTO();
        editRoomType.setId(2L);

        when(request.getMethod()).thenReturn("GET");
        when(request.getParameter("editId")).thenReturn(null);
        when(request.getParameter("editTypeId")).thenReturn("2");
        when(roomTypeService.findById(2L)).thenReturn(editRoomType);
        when(roomService.findAll()).thenReturn(Collections.emptyList());
        when(roomTypeService.findAll()).thenReturn(Collections.emptyList());
        when(request.getRequestDispatcher("/admin/rooms.jsp")).thenReturn(dispatcher);

        controller.service(request, response);

        verify(roomTypeService).findById(2L);
        verify(request).setAttribute("editRoomType", editRoomType);
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void doGet_withInvalidEditTypeId_ignoresNumberFormat() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getParameter("editId")).thenReturn(null);
        when(request.getParameter("editTypeId")).thenReturn("bad");
        when(roomService.findAll()).thenReturn(Collections.emptyList());
        when(roomTypeService.findAll()).thenReturn(Collections.emptyList());
        when(request.getRequestDispatcher("/admin/rooms.jsp")).thenReturn(dispatcher);

        controller.service(request, response);

        verify(roomTypeService, never()).findById(anyLong());
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

        verify(roomService).delete(5L);
        verify(session).setAttribute("flashSuccess", "Room deleted successfully.");
        verify(response).sendRedirect(CTX + "/rooms");
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
        when(request.getParameter("roomNumber")).thenReturn("");
        when(request.getParameter("roomTypeId")).thenReturn("1");
        when(request.getParameter("floor")).thenReturn("2");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);

        controller.service(request, response);

        verify(session).setAttribute(eq("fieldErrors"), argThat(errors ->
                "Room number is required.".equals(((java.util.Map<?, ?>) errors).get("roomNumber"))));
        verify(response).sendRedirect(CTX + "/rooms?editId=3");
        verify(roomService, never()).update(any());
    }

    @Test
    public void doPost_update_roomTypeIdBlank_setsFieldError() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("id")).thenReturn("3");
        when(request.getParameter("roomNumber")).thenReturn("101");
        when(request.getParameter("roomTypeId")).thenReturn("");
        when(request.getParameter("floor")).thenReturn("1");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);

        controller.service(request, response);

        verify(session).setAttribute(eq("fieldErrors"), argThat(errors ->
                "Room type is required.".equals(((java.util.Map<?, ?>) errors).get("roomTypeId"))));
        verify(response).sendRedirect(CTX + "/rooms?editId=3");
    }

    @Test
    public void doPost_update_roomTypeIdZero_setsFieldError() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("id")).thenReturn("3");
        when(request.getParameter("roomNumber")).thenReturn("101");
        when(request.getParameter("roomTypeId")).thenReturn("0");
        when(request.getParameter("floor")).thenReturn("1");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);

        controller.service(request, response);

        verify(session).setAttribute(eq("fieldErrors"), argThat(errors ->
                "Room type is required.".equals(((java.util.Map<?, ?>) errors).get("roomTypeId"))));
    }

    @Test
    public void doPost_update_floorBlank_setsFieldError() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("id")).thenReturn("3");
        when(request.getParameter("roomNumber")).thenReturn("101");
        when(request.getParameter("roomTypeId")).thenReturn("1");
        when(request.getParameter("floor")).thenReturn("");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);

        controller.service(request, response);

        verify(session).setAttribute(eq("fieldErrors"), argThat(errors ->
                "Floor is required.".equals(((java.util.Map<?, ?>) errors).get("floor"))));
        verify(response).sendRedirect(CTX + "/rooms?editId=3");
    }

    @Test
    public void doPost_update_valid_callsUpdateAndRedirects() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("id")).thenReturn("3");
        when(request.getParameter("roomNumber")).thenReturn("201");
        when(request.getParameter("roomTypeId")).thenReturn("2");
        when(request.getParameter("floor")).thenReturn("2");
        when(request.getParameter("status")).thenReturn("OCCUPIED");
        when(request.getParameter("fullAccess")).thenReturn("on");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);

        controller.service(request, response);

        verify(roomService).update(argThat(d ->
                d.getId() == 3L && "201".equals(d.getRoomNumber()) && d.getRoomTypeId() == 2L
                        && d.getFloor() == 2 && "OCCUPIED".equals(d.getStatus()) && d.isFullAccess()));
        verify(session).setAttribute("flashSuccess", "Room updated successfully.");
        verify(response).sendRedirect(CTX + "/rooms");
    }

    @Test
    public void doPost_update_statusBlank_defaultsToAvailable() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("id")).thenReturn("4");
        when(request.getParameter("roomNumber")).thenReturn("301");
        when(request.getParameter("roomTypeId")).thenReturn("1");
        when(request.getParameter("floor")).thenReturn("3");
        when(request.getParameter("status")).thenReturn("");
        when(request.getParameter("fullAccess")).thenReturn(null);
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);

        controller.service(request, response);

        verify(roomService).update(argThat(d -> "AVAILABLE".equals(d.getStatus()) && !d.isFullAccess()));
    }

    @Test
    public void doPost_update_roomTypeIdZeroAfterValidation_throwsAndSetsFlashError() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        // Validation: roomTypeId "0" triggers "Room type is required." So we need a different way to hit
        // "if (roomTypeId <= 0) throw". We need roomTypeId that is not blank and not "0" but parses to <= 0.
        // The only option is negative: "-1". But wait, the parameter is "roomTypeId" - can it be "-1"? Yes.
        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("id")).thenReturn("3");
        when(request.getParameter("roomNumber")).thenReturn("101");
        when(request.getParameter("roomTypeId")).thenReturn("-1");
        when(request.getParameter("floor")).thenReturn("1");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);

        controller.service(request, response);

        verify(session).setAttribute("flashError", "Select a valid room type.");
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
        when(request.getParameter("roomNumber")).thenReturn("");
        when(request.getParameter("roomTypeId")).thenReturn("1");
        when(request.getParameter("floor")).thenReturn("1");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);

        controller.service(request, response);

        verify(session).setAttribute(eq("fieldErrors"), any());
        verify(response).sendRedirect(CTX + "/rooms");
        verify(roomService, never()).create(any());
    }

    @Test
    public void doPost_create_valid_callsCreateAndRedirects() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn(null);
        when(request.getParameter("roomNumber")).thenReturn("401");
        when(request.getParameter("roomTypeId")).thenReturn("2");
        when(request.getParameter("floor")).thenReturn("4");
        when(request.getParameter("fullAccess")).thenReturn("on");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);

        controller.service(request, response);

        verify(roomService).create(argThat(d ->
                "401".equals(d.getRoomNumber()) && d.getRoomTypeId() == 2L && d.getFloor() == 4
                        && "AVAILABLE".equals(d.getStatus()) && d.isFullAccess()));
        verify(session).setAttribute("flashSuccess", "Room created successfully.");
        verify(response).sendRedirect(CTX + "/rooms");
    }

    @Test
    public void doPost_create_roomTypeIdInvalid_throwsAndSetsFlashError() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn(null);
        when(request.getParameter("roomNumber")).thenReturn("501");
        when(request.getParameter("roomTypeId")).thenReturn("-1");
        when(request.getParameter("floor")).thenReturn("5");
        when(request.getContextPath()).thenReturn(CTX);
        when(request.getSession()).thenReturn(session);

        controller.service(request, response);

        verify(session).setAttribute("flashError", "Select a valid room type.");
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
        doThrow(new RuntimeException("DB error")).when(roomService).delete(1L);

        controller.service(request, response);

        verify(session).setAttribute("flashError", "DB error");
        verify(response).sendRedirect(CTX + "/rooms");
    }
}
