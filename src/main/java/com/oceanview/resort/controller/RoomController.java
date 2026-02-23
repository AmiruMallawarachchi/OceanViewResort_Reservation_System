package com.oceanview.resort.controller;

import com.oceanview.resort.dto.RoomDTO;
import com.oceanview.resort.dto.RoomTypeDTO;
import com.oceanview.resort.factory.ServiceFactory;
import com.oceanview.resort.service.RoomService;
import com.oceanview.resort.service.RoomTypeService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class RoomController extends HttpServlet {
    private final RoomService roomService = ServiceFactory.getInstance().getRoomService();
    private final RoomTypeService roomTypeService = ServiceFactory.getInstance().getRoomTypeService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String editId = request.getParameter("editId");
        if (editId != null) {
            try {
                long id = Long.parseLong(editId);
                RoomDTO editRoom = roomService.findById(id);
                request.setAttribute("editRoom", editRoom);
            } catch (NumberFormatException ignored) {
            }
        }

        String editTypeId = request.getParameter("editTypeId");
        if (editTypeId != null) {
            try {
                long id = Long.parseLong(editTypeId);
                RoomTypeDTO editRoomType = roomTypeService.findById(id);
                request.setAttribute("editRoomType", editRoomType);
            } catch (NumberFormatException ignored) {
            }
        }

        List<RoomDTO> rooms = roomService.findAll();
        List<RoomTypeDTO> roomTypes = roomTypeService.findAll();
        request.setAttribute("rooms", rooms);
        request.setAttribute("roomTypes", roomTypes);
        request.getRequestDispatcher("/admin/rooms.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        java.util.Map<String, String> errors = new java.util.HashMap<>();
        try {
            if ("delete".equalsIgnoreCase(action)) {
                long id = Long.parseLong(request.getParameter("id"));
                roomService.delete(id);
                request.getSession().setAttribute("flashSuccess", "Room deleted successfully.");
            } else if ("toggleMaintenance".equalsIgnoreCase(action)) {
                long id = Long.parseLong(request.getParameter("id"));
                RoomDTO room = roomService.findById(id);
                if (room == null) {
                    request.getSession().setAttribute("flashError", "Room not found.");
                } else {
                    String currentStatus = room.getStatus() == null ? "AVAILABLE" : room.getStatus();
                    String nextStatus = "MAINTENANCE".equalsIgnoreCase(currentStatus) ? "AVAILABLE" : "MAINTENANCE";
                    room.setStatus(nextStatus);
                    roomService.update(room);
                    request.getSession().setAttribute("flashSuccess",
                            "Room status updated to " + nextStatus + ".");
                }
            } else if ("update".equalsIgnoreCase(action)) {
                validateRoomFields(request, errors);
                if (!errors.isEmpty()) {
                    request.getSession().setAttribute("fieldErrors", errors);
                    response.sendRedirect(request.getContextPath() + "/rooms?editId=" + request.getParameter("id"));
                    return;
                }
                RoomDTO dto = new RoomDTO();
                dto.setId(Long.parseLong(request.getParameter("id")));
                dto.setRoomNumber(request.getParameter("roomNumber"));
                long roomTypeId = Long.parseLong(request.getParameter("roomTypeId"));
                if (roomTypeId <= 0) {
                    throw new IllegalArgumentException("Select a valid room type.");
                }
                dto.setRoomTypeId(roomTypeId);
                dto.setFloor(Integer.parseInt(request.getParameter("floor")));
                String status = request.getParameter("status");
                dto.setStatus(status == null || status.isBlank() ? "AVAILABLE" : status);
                dto.setFullAccess(request.getParameter("fullAccess") != null);
                roomService.update(dto);
                request.getSession().setAttribute("flashSuccess", "Room updated successfully.");
            } else {
                validateRoomFields(request, errors);
                if (!errors.isEmpty()) {
                    request.getSession().setAttribute("fieldErrors", errors);
                    response.sendRedirect(request.getContextPath() + "/rooms");
                    return;
                }
        RoomDTO dto = new RoomDTO();
        dto.setRoomNumber(request.getParameter("roomNumber"));
                long roomTypeId = Long.parseLong(request.getParameter("roomTypeId"));
                if (roomTypeId <= 0) {
                    throw new IllegalArgumentException("Select a valid room type.");
                }
                dto.setRoomTypeId(roomTypeId);
        dto.setFloor(Integer.parseInt(request.getParameter("floor")));
        dto.setStatus("AVAILABLE");
        dto.setFullAccess(request.getParameter("fullAccess") != null);
        roomService.create(dto);
                request.getSession().setAttribute("flashSuccess", "Room created successfully.");
            }
        } catch (Exception ex) {
            request.getSession().setAttribute("flashError", ex.getMessage());
        }
        response.sendRedirect(request.getContextPath() + "/rooms");
    }

    private void validateRoomFields(HttpServletRequest request, java.util.Map<String, String> errors) {
        if (isBlank(request.getParameter("roomNumber"))) {
            errors.put("roomNumber", "Room number is required.");
        }
        if (isBlank(request.getParameter("roomTypeId")) || "0".equals(request.getParameter("roomTypeId"))) {
            errors.put("roomTypeId", "Room type is required.");
        }
        if (isBlank(request.getParameter("floor"))) {
            errors.put("floor", "Floor is required.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
