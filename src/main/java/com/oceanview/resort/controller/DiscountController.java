package com.oceanview.resort.controller;

import com.oceanview.resort.dto.DiscountDTO;
import com.oceanview.resort.factory.ServiceFactory;
import com.oceanview.resort.service.DiscountService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class DiscountController extends HttpServlet {
    private final DiscountService discountService = ServiceFactory.getInstance().getDiscountService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String editId = request.getParameter("editId");
        if (editId != null) {
            try {
                long id = Long.parseLong(editId);
                DiscountDTO editDiscount = discountService.findById(id);
                request.setAttribute("editDiscount", editDiscount);
            } catch (NumberFormatException ignored) {
            }
        }
        List<DiscountDTO> discounts = discountService.findAll();
        request.setAttribute("discounts", discounts);
        request.getRequestDispatcher("/admin/discounts.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        java.util.Map<String, String> errors = new java.util.HashMap<>();
        try {
            if ("delete".equalsIgnoreCase(action)) {
                long id = Long.parseLong(request.getParameter("id"));
                discountService.delete(id);
                request.getSession().setAttribute("flashSuccess", "Discount deleted successfully.");
            } else if ("toggle".equalsIgnoreCase(action)) {
                long id = Long.parseLong(request.getParameter("id"));
                DiscountDTO existing = discountService.findById(id);
                if (existing != null) {
                    existing.setActive(!existing.isActive());
                    discountService.update(existing);
                    request.getSession().setAttribute("flashSuccess", "Discount status updated.");
                }
            } else if ("update".equalsIgnoreCase(action)) {
                validateDiscount(request, errors);
                if (!errors.isEmpty()) {
                    request.getSession().setAttribute("fieldErrors", errors);
                    response.sendRedirect(request.getContextPath() + "/discounts?editId=" + request.getParameter("id"));
                    return;
                }
                DiscountDTO dto = buildDiscountDTO(request);
                dto.setId(Long.parseLong(request.getParameter("id")));
                discountService.update(dto);
                request.getSession().setAttribute("flashSuccess", "Discount updated successfully.");
            } else {
                validateDiscount(request, errors);
                if (!errors.isEmpty()) {
                    request.getSession().setAttribute("fieldErrors", errors);
                    response.sendRedirect(request.getContextPath() + "/discounts");
                    return;
                }
                DiscountDTO dto = buildDiscountDTO(request);
                discountService.create(dto);
                request.getSession().setAttribute("flashSuccess", "Discount created successfully.");
            }
        } catch (Exception ex) {
            request.getSession().setAttribute("flashError", ex.getMessage());
        }
        response.sendRedirect(request.getContextPath() + "/discounts");
    }

    private void validateDiscount(HttpServletRequest request, java.util.Map<String, String> errors) {
        if (isBlank(request.getParameter("name"))) {
            errors.put("name", "Discount name is required.");
        }
        String discountType = request.getParameter("discountType");
        if (isBlank(discountType)) {
            errors.put("discountType", "Discount type is required.");
        } else if ("GUEST_TYPE".equals(discountType) && isBlank(request.getParameter("guestType"))) {
            errors.put("guestType", "Guest type is required for guest discounts.");
        }
        String percent = request.getParameter("percent");
        if (isBlank(percent)) {
            errors.put("percent", "Discount percent is required.");
        } else {
            try {
                BigDecimal value = new BigDecimal(percent);
                if (value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(BigDecimal.valueOf(100)) > 0) {
                    errors.put("percent", "Discount percent must be between 0 and 100.");
                }
            } catch (NumberFormatException ex) {
                errors.put("percent", "Discount percent must be a valid number.");
            }
        }
    }

    private DiscountDTO buildDiscountDTO(HttpServletRequest request) {
        DiscountDTO dto = new DiscountDTO();
        dto.setName(request.getParameter("name"));
        String discountType = request.getParameter("discountType");
        dto.setDiscountType(discountType);
        if ("GUEST_TYPE".equals(discountType)) {
            dto.setGuestType(request.getParameter("guestType"));
        } else {
            dto.setGuestType(null);
        }
        dto.setPercent(request.getParameter("percent"));
        dto.setDescription(request.getParameter("description"));
        dto.setActive(Boolean.parseBoolean(request.getParameter("active")));
        return dto;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
