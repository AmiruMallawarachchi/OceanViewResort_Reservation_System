package com.oceanview.resort.service;

import com.oceanview.resort.dto.BillDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface BillService {
    BillDTO generate(long reservationId, BigDecimal manualDiscountPercent, List<Long> discountIds, LocalDate actualCheckoutDate, long generatedById);
    BillDTO findById(long id);
    BillDTO findByReservationId(long reservationId);
    List<BillDTO> findAll();
}
