package com.oceanview.resort.repository;

import com.oceanview.resort.model.Bill;

import java.util.List;

public interface BillRepository {
    Bill create(Bill bill);
    Bill findById(long id);
    Bill findByReservationId(long reservationId);
    List<Bill> findAll();
}
