package com.oceanview.resort.repository;

import com.oceanview.resort.model.Reservation;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository {
    Reservation create(Reservation reservation);
    Reservation update(Reservation reservation);
    boolean delete(long id);
    Reservation findById(long id);
    Reservation findByReservationNo(String reservationNo);
    List<Reservation> findAll();
    List<Reservation> search(String keyword);
    List<Reservation> findWithFilters(String keyword, LocalDate fromDate, LocalDate toDate, String status);
    boolean isRoomAvailable(long roomId, LocalDate checkIn, LocalDate checkOut);
}
