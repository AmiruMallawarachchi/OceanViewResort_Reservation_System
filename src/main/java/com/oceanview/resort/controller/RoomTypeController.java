package com.oceanview.resort.controller;

import com.oceanview.resort.dto.RoomTypeDTO;
import com.oceanview.resort.factory.ServiceFactory;
import com.oceanview.resort.service.RoomTypeService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RoomTypeController extends HttpServlet {
    private final RoomTypeService roomTypeService = ServiceFactory.getInstance().getRoomTypeService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        java.util.Map<String, String> errors = new java.util.HashMap<>();
        try {
            if ("delete".equalsIgnoreCase(action)) {
                long id = Long.parseLong(request.getParameter("id"));
                roomTypeService.delete(id);
                request.getSession().setAttribute("flashSuccess", "Room type deleted successfully.");
            } else if ("update".equalsIgnoreCase(action)) {
                validateTypeFields(request, errors);
                if (!errors.isEmpty()) {
                    request.getSession().setAttribute("fieldErrors", errors);
                    response.sendRedirect(request.getContextPath() + "/rooms?editTypeId=" + request.getParameter("id"));
                    return;
                }
                RoomTypeDTO dto = new RoomTypeDTO();
                dto.setId(Long.parseLong(request.getParameter("id")));
                dto.setTypeName(request.getParameter("typeName"));
                dto.setRatePerNight(request.getParameter("ratePerNight"));
                dto.setAmenities(request.getParameter("amenities"));
                dto.setMaxOccupancy(Integer.parseInt(request.getParameter("maxOccupancy")));
                dto.setActive(true);
                roomTypeService.update(dto);
                request.getSession().setAttribute("flashSuccess", "Room type updated successfully.");
            } else {
                validateTypeFields(request, errors);
                if (!errors.isEmpty()) {
                    request.getSession().setAttribute("fieldErrors", errors);
                    response.sendRedirect(request.getContextPath() + "/rooms");
                    return;
                }
                RoomTypeDTO dto = new RoomTypeDTO();
                dto.setTypeName(request.getParameter("typeName"));
                dto.setRatePerNight(request.getParameter("ratePerNight"));
                dto.setAmenities(request.getParameter("amenities"));
                dto.setMaxOccupancy(Integer.parseInt(request.getParameter("maxOccupancy")));
                dto.setActive(true);
                roomTypeService.create(dto);
                request.getSession().setAttribute("flashSuccess", "Room type created successfully.");
            }
        } catch (Exception ex) {
            request.getSession().setAttribute("flashError", ex.getMessage());
        }
        response.sendRedirect(request.getContextPath() + "/rooms");
    }

    private void validateTypeFields(HttpServletRequest request, java.util.Map<String, String> errors) {
        if (isBlank(request.getParameter("typeName"))) {
            errors.put("typeName", "Type name is required.");
        }
        String rateStr = request.getParameter("ratePerNight");
        if (isBlank(rateStr)) {
            errors.put("ratePerNight", "Rate per night is required.");
        } else {
            try {
                double rate = Double.parseDouble(rateStr.trim());
                if (rate < 0) {
                    errors.put("ratePerNight", "Rate per night cannot be negative.");
                }
            } catch (NumberFormatException e) {
                errors.put("ratePerNight", "Rate per night must be a valid number.");
            }
        }
        String maxOccStr = request.getParameter("maxOccupancy");
        if (isBlank(maxOccStr)) {
            errors.put("maxOccupancy", "Max occupancy is required.");
        } else {
            try {
                int maxOcc = Integer.parseInt(maxOccStr.trim());
                if (maxOcc <= 0) {
                    errors.put("maxOccupancy", "Max occupancy must be at least 1.");
                }
            } catch (NumberFormatException e) {
                errors.put("maxOccupancy", "Max occupancy must be a valid number.");
            }
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
