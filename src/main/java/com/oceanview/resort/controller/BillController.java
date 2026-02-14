package com.oceanview.resort.controller;

import com.oceanview.resort.dto.BillDTO;
import com.oceanview.resort.dto.DiscountDTO;
import com.oceanview.resort.dto.ReservationDTO;
import com.oceanview.resort.factory.ServiceFactory;
import com.oceanview.resort.service.BillService;
import com.oceanview.resort.service.DiscountService;
import com.oceanview.resort.service.ReservationService;
import com.oceanview.resort.util.DateUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BillController extends HttpServlet {
    private final BillService billService = ServiceFactory.getInstance().getBillService();
    private final ReservationService reservationService = ServiceFactory.getInstance().getReservationService();
    private final DiscountService discountService = ServiceFactory.getInstance().getDiscountService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String query = request.getParameter("q");
        if (query != null && !query.isBlank()) {
            List<ReservationDTO> reservations = reservationService.search(query);
            request.setAttribute("billingReservations", reservations);
            request.setAttribute("searchQuery", query);
        }

        List<DiscountDTO> activeDiscounts = discountService.findActive();
        request.setAttribute("activeDiscounts", activeDiscounts);
        try {
            request.getRequestDispatcher("/reservationist/billing.jsp").forward(request, response);
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        try {
            String reservationValue = request.getParameter("reservationId");
            if (reservationValue == null || reservationValue.isBlank()) {
                java.util.Map<String, String> errors = new java.util.HashMap<>();
                errors.put("reservationId", "Reservation ID is required.");
                request.getSession().setAttribute("fieldErrors", errors);
                response.sendRedirect(request.getContextPath() + "/bills");
                return;
            }
            long reservationId = Long.parseLong(reservationValue);
            BigDecimal manualDiscount = parseDiscount(request.getParameter("manualDiscountPercent"));
            List<Long> discountIds = parseDiscountIds(request.getParameterValues("discountIds"));
            LocalDate actualCheckout = DateUtil.parseDate(request.getParameter("actualCheckoutDate"));
            long generatedBy = getCurrentUserId(request.getSession(false));
            BillDTO bill = billService.generate(reservationId, manualDiscount, discountIds, actualCheckout, generatedBy);
            if (bill == null) {
                request.getSession().setAttribute("flashError", "Reservation not found.");
            } else {
                request.getSession().setAttribute("flashSuccess", "Bill generated successfully.");
                request.getSession().setAttribute("lastBill", bill);
            }
        } catch (Exception ex) {
            request.getSession().setAttribute("flashError", ex.getMessage());
        }
        response.sendRedirect(request.getContextPath() + "/bills");
    }

    private long getCurrentUserId(HttpSession session) {
        if (session == null || session.getAttribute("userId") == null) {
            return 1;
        }
        return (long) session.getAttribute("userId");
    }

    private BigDecimal parseDiscount(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        BigDecimal parsed = new BigDecimal(value);
        if (parsed.compareTo(BigDecimal.ZERO) < 0 || parsed.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Manual discount must be between 0 and 100.");
        }
        return parsed;
    }

    private List<Long> parseDiscountIds(String[] values) {
        if (values == null || values.length == 0) {
            return new ArrayList<>();
        }
        List<Long> ids = new ArrayList<>();
        Arrays.stream(values).forEach(value -> {
            try {
                ids.add(Long.parseLong(value));
            } catch (NumberFormatException ignored) {
            }
        });
        return ids;
    }
}
