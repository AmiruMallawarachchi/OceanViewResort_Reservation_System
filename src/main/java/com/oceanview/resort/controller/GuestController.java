package com.oceanview.resort.controller;

import com.oceanview.resort.dao.GuestDAO;
import com.oceanview.resort.dto.GuestDTO;
import com.oceanview.resort.factory.ServiceFactory;
import com.oceanview.resort.service.GuestService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class GuestController extends HttpServlet {
    private final GuestService guestService = ServiceFactory.getInstance().getGuestService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String editId = request.getParameter("editId");
        if (editId != null) {
            try {
                long id = Long.parseLong(editId);
                GuestDTO editGuest = guestService.findById(id);
                request.setAttribute("editGuest", editGuest);
            } catch (NumberFormatException ignored) {
            }
        }
        List<GuestDTO> guests = guestService.findAll();
        request.setAttribute("guests", guests);
        request.getRequestDispatcher("/reservationist/guests.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        java.util.Map<String, String> errors = new java.util.HashMap<>();
        try {
            if ("delete".equalsIgnoreCase(action)) {
                long id = Long.parseLong(request.getParameter("id"));
                guestService.deleteGuest(id);
                request.getSession().setAttribute("flashSuccess", "Guest deleted successfully.");
            } else if ("update".equalsIgnoreCase(action)) {
                long id = Long.parseLong(request.getParameter("id"));
                validateGuest(request, errors);
                String idNumber = trimToNull(request.getParameter("idNumber"));
                if (idNumber != null) {
                    GuestDTO existing = guestService.findByIdNumber(idNumber);
                    if (existing != null && existing.getId() != id) {
                        errors.put("idNumber", "ID number already exists.");
                    }
                }
                if (!errors.isEmpty()) {
                    request.getSession().setAttribute("fieldErrors", errors);
                    response.sendRedirect(request.getContextPath() + "/guests?editId=" + request.getParameter("id"));
                    return;
                }
                GuestDTO dto = buildGuestDTO(request);
                dto.setId(id);
                guestService.updateGuest(dto);
                request.getSession().setAttribute("flashSuccess", "Guest updated successfully.");
            } else {
                validateGuest(request, errors);
                String idNumber = trimToNull(request.getParameter("idNumber"));
                if (idNumber != null) {
                    GuestDTO existing = guestService.findByIdNumber(idNumber);
                    if (existing != null) {
                        errors.put("idNumber", "ID number already exists.");
                    }
                }
                if (!errors.isEmpty()) {
                    request.getSession().setAttribute("fieldErrors", errors);
                    response.sendRedirect(request.getContextPath() + "/guests");
                    return;
                }
                GuestDTO dto = buildGuestDTO(request);
                guestService.createGuest(dto);
                request.getSession().setAttribute("flashSuccess", "Guest created successfully.");
            }
        } catch (Exception ex) {
            request.getSession().setAttribute("flashError", ex.getMessage());
        }
        response.sendRedirect(request.getContextPath() + "/guests");
    }

    private GuestDTO buildGuestDTO(HttpServletRequest request) {
        GuestDTO dto = new GuestDTO();
        dto.setFullName(request.getParameter("fullName"));
        dto.setEmail(request.getParameter("email"));
        dto.setPhone(request.getParameter("phone"));
        dto.setAddress(request.getParameter("address"));
        dto.setIdType(request.getParameter("idType"));
        dto.setIdNumber(request.getParameter("idNumber"));
        dto.setNationality(request.getParameter("nationality"));
        String guestType = request.getParameter("guestType");
        dto.setGuestType(guestType == null || guestType.isBlank() ? "REGULAR" : guestType);
        return dto;
    }

    private void validateGuest(HttpServletRequest request, java.util.Map<String, String> errors) {
        if (isBlank(request.getParameter("fullName"))) {
            errors.put("fullName", "Full name is required.");
        }
        if (isBlank(request.getParameter("phone"))) {
            errors.put("phone", "Phone is required.");
        }
        if (isBlank(request.getParameter("address"))) {
            errors.put("address", "Address is required.");
        }
        String guestType = request.getParameter("guestType");
        if (!isBlank(guestType)) {
            try {
                com.oceanview.resort.model.enums.GuestType.valueOf(guestType);
            } catch (IllegalArgumentException ex) {
                errors.put("guestType", "Select a valid guest type.");
            }
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
