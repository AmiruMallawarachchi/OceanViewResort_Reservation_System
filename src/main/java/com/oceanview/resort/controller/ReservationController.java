package com.oceanview.resort.controller;

import com.oceanview.resort.dto.ReservationDTO;
import com.oceanview.resort.dto.RoomDTO;
import com.oceanview.resort.dto.RoomTypeDTO;
import com.oceanview.resort.factory.ServiceFactory;
import com.oceanview.resort.service.ReservationService;
import com.oceanview.resort.service.RoomService;
import com.oceanview.resort.service.RoomTypeService;
import com.oceanview.resort.util.DateUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

public class ReservationController extends HttpServlet {
    private final ReservationService reservationService = ServiceFactory.getInstance().getReservationService();
    private final RoomService roomService = ServiceFactory.getInstance().getRoomService();
    private final RoomTypeService roomTypeService = ServiceFactory.getInstance().getRoomTypeService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if ("1".equals(request.getParameter("clearSuccess"))) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.removeAttribute("flashReservationNo");
                session.removeAttribute("flashReservationId");
            }
        }
        String viewId = request.getParameter("viewId");
        if (viewId != null) {
            try {
                long id = Long.parseLong(viewId);
                ReservationDTO viewReservation = reservationService.findById(id);
                if (viewReservation != null) {
                    request.setAttribute("viewReservation", viewReservation);
                    request.setAttribute("isPrint", "1".equals(request.getParameter("print")));
                    request.getRequestDispatcher("/reservationist/reservation-detail.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        String editId = request.getParameter("editId");
        if (editId != null) {
            try {
                long id = Long.parseLong(editId);
                ReservationDTO editReservation = reservationService.findById(id);
                request.setAttribute("editReservation", editReservation);
            } catch (NumberFormatException ignored) {
            }
        }
        String query = trimToNull(request.getParameter("q"));
        String fromDateStr = trimToNull(request.getParameter("fromDate"));
        String toDateStr = trimToNull(request.getParameter("toDate"));
        String statusFilter = trimToNull(request.getParameter("status"));
        List<ReservationDTO> reservations;
        if (fromDateStr != null || toDateStr != null || statusFilter != null || (query != null && !query.isBlank())) {
            LocalDate fromDate = DateUtil.parseDate(fromDateStr);
            LocalDate toDate = DateUtil.parseDate(toDateStr);
            reservations = reservationService.findWithFilters(query, fromDate, toDate, statusFilter);
            request.setAttribute("searchQuery", query);
            request.setAttribute("filterFromDate", fromDateStr);
            request.setAttribute("filterToDate", toDateStr);
            request.setAttribute("filterStatus", statusFilter);
        } else if (query != null && !query.isBlank()) {
            reservations = reservationService.search(query);
            request.setAttribute("searchQuery", query);
        } else {
            reservations = reservationService.findAll();
        }
        request.setAttribute("reservations", reservations);
        loadAvailabilityData(request);
        request.getRequestDispatcher("/reservationist/reservations.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        java.util.Map<String, String> errors = new java.util.HashMap<>();
        try {
            if ("cancel".equalsIgnoreCase(action)) {
                long id = Long.parseLong(request.getParameter("id"));
                ReservationDTO existing = reservationService.findById(id);
                if (existing != null) {
                    existing.setStatus("CANCELLED");
                    reservationService.update(existing);
                }
                request.getSession().setAttribute("flashSuccess", "Reservation cancelled.");
            } else if ("checkIn".equalsIgnoreCase(action)) {
                long id = Long.parseLong(request.getParameter("id"));
                ReservationDTO existing = reservationService.findById(id);
                if (existing != null) {
                    existing.setStatus("CHECKED_IN");
                    reservationService.update(existing);
                    request.getSession().setAttribute("flashSuccess", "Guest checked in.");
                } else {
                    request.getSession().setAttribute("flashError", "Reservation not found.");
                }
            } else if ("checkOut".equalsIgnoreCase(action)) {
                long id = Long.parseLong(request.getParameter("id"));
                ReservationDTO existing = reservationService.findById(id);
                if (existing != null) {
                    existing.setStatus("CHECKED_OUT");
                    reservationService.update(existing);
                    request.getSession().setAttribute("flashSuccess", "Guest checked out.");
                } else {
                    request.getSession().setAttribute("flashError", "Reservation not found.");
                }
            } else if ("update".equalsIgnoreCase(action)) {
                validateReservationFields(request, errors, "update");
                if (!errors.isEmpty()) {
                    request.getSession().setAttribute("fieldErrors", errors);
                    response.sendRedirect(request.getContextPath() + "/reservations?editId=" + request.getParameter("id"));
                    return;
                }
                ReservationDTO dto = new ReservationDTO();
                dto.setId(Long.parseLong(request.getParameter("id")));
                dto.setGuestId(Long.parseLong(request.getParameter("guestId")));
                dto.setRoomId(Long.parseLong(request.getParameter("roomId")));
                dto.setCheckInDate(request.getParameter("checkInDate"));
                dto.setCheckOutDate(request.getParameter("checkOutDate"));
                dto.setStatus(request.getParameter("status"));
                reservationService.update(dto);
                request.getSession().setAttribute("flashSuccess", "Reservation updated successfully.");
            } else {
                validateReservationFields(request, errors, "create");
                if (!errors.isEmpty()) {
                    request.getSession().setAttribute("fieldErrors", errors);
                    response.sendRedirect(request.getContextPath() + "/reservations");
                    return;
                }
        ReservationDTO dto = new ReservationDTO();
        dto.setGuestId(Long.parseLong(request.getParameter("guestId")));
        dto.setRoomId(Long.parseLong(request.getParameter("roomId")));
        dto.setCheckInDate(request.getParameter("checkInDate"));
        dto.setCheckOutDate(request.getParameter("checkOutDate"));
        dto.setStatus("PENDING");

        long createdBy = getCurrentUserId(request.getSession(false));
        ReservationDTO created = reservationService.create(dto, createdBy);
                request.getSession().setAttribute("flashSuccess", "Reservation created successfully.");
                if (created != null) {
                    if (created.getReservationNo() != null) {
                        request.getSession().setAttribute("flashReservationNo", created.getReservationNo());
                    }
                    request.getSession().setAttribute("flashReservationId", created.getId());
                }
            }
        } catch (Exception ex) {
            request.getSession().setAttribute("flashError", ex.getMessage());
        }
        String redirectUrl = request.getContextPath() + "/reservations" + buildAvailabilityQueryString(request);
        response.sendRedirect(redirectUrl);
    }

    /** Preserve availability params (availCheckIn, availCheckOut, roomTypeId, roomQ) on redirect so success message is visible in same context. */
    private String buildAvailabilityQueryString(HttpServletRequest request) {
        String checkIn = trimToNull(request.getParameter("availCheckIn"));
        String checkOut = trimToNull(request.getParameter("availCheckOut"));
        String roomTypeId = trimToNull(request.getParameter("roomTypeId"));
        String roomQ = trimToNull(request.getParameter("roomQ"));
        if (checkIn == null && checkOut == null && roomTypeId == null && roomQ == null) {
            return "";
        }
        StringBuilder qs = new StringBuilder("?");
        if (checkIn != null) qs.append("availCheckIn=").append(URLEncoder.encode(checkIn, StandardCharsets.UTF_8)).append("&");
        if (checkOut != null) qs.append("availCheckOut=").append(URLEncoder.encode(checkOut, StandardCharsets.UTF_8)).append("&");
        if (roomTypeId != null) qs.append("roomTypeId=").append(URLEncoder.encode(roomTypeId, StandardCharsets.UTF_8)).append("&");
        if (roomQ != null) qs.append("roomQ=").append(URLEncoder.encode(roomQ, StandardCharsets.UTF_8)).append("&");
        return qs.substring(0, qs.length() - 1);
    }

    private void validateReservationFields(HttpServletRequest request, java.util.Map<String, String> errors, String mode) {
        if (isBlank(request.getParameter("guestId"))) {
            errors.put("guestId", "Guest ID is required.");
        }
        if (isBlank(request.getParameter("roomId"))) {
            errors.put("roomId", "Room ID is required.");
        }
        if (isBlank(request.getParameter("checkInDate"))) {
            errors.put("checkInDate", "Check-in date is required.");
        }
        if (isBlank(request.getParameter("checkOutDate"))) {
            errors.put("checkOutDate", "Check-out date is required.");
        }
        String checkInStr = trimToNull(request.getParameter("checkInDate"));
        String checkOutStr = trimToNull(request.getParameter("checkOutDate"));
        if (checkInStr != null && checkOutStr != null) {
            LocalDate checkIn = DateUtil.parseDate(checkInStr);
            LocalDate checkOut = DateUtil.parseDate(checkOutStr);
            if (checkIn != null && checkOut != null) {
                if (!checkOut.isAfter(checkIn)) {
                    errors.put("checkOutDate", "Check-out date must be after check-in date.");
                } else if ("create".equals(mode) && checkIn.isBefore(LocalDate.now())) {
                    errors.put("checkInDate", "Check-in date cannot be in the past for a new reservation.");
                }
            }
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private void loadAvailabilityData(HttpServletRequest request) {
        List<RoomTypeDTO> roomTypes = roomTypeService.findAll();
        request.setAttribute("roomTypes", roomTypes);

        String checkInValue = trimToNull(request.getParameter("availCheckIn"));
        String checkOutValue = trimToNull(request.getParameter("availCheckOut"));
        String roomTypeValue = trimToNull(request.getParameter("roomTypeId"));
        String roomQuery = trimToNull(request.getParameter("roomQ"));

        request.setAttribute("availabilityCheckIn", checkInValue);
        request.setAttribute("availabilityCheckOut", checkOutValue);
        request.setAttribute("availabilityRoomTypeId", roomTypeValue);
        request.setAttribute("availabilityRoomQuery", roomQuery);

        if (checkInValue == null || checkOutValue == null) {
            return;
        }

        LocalDate checkIn = DateUtil.parseDate(checkInValue);
        LocalDate checkOut = DateUtil.parseDate(checkOutValue);
        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            request.setAttribute("availabilityError", "Select a valid date range to view availability.");
            return;
        }

        List<RoomDTO> availableRooms = roomService.findAvailable(checkIn, checkOut);
        if (roomTypeValue != null) {
            try {
                long roomTypeId = Long.parseLong(roomTypeValue);
                availableRooms.removeIf(room -> room.getRoomTypeId() != roomTypeId);
            } catch (NumberFormatException ignored) {
            }
        }
        if (roomQuery != null) {
            String query = roomQuery.toLowerCase();
            availableRooms.removeIf(room -> room.getRoomNumber() == null || !room.getRoomNumber().toLowerCase().contains(query));
        }
        request.setAttribute("availableRooms", availableRooms);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private long getCurrentUserId(HttpSession session) {
        if (session == null || session.getAttribute("userId") == null) {
            throw new IllegalStateException("User must be logged in to perform this action.");
        }
        return (long) session.getAttribute("userId");
    }
}
