package com.oceanview.resort.service;

import com.oceanview.resort.dto.ReservationDTO;

import java.util.List;

public interface ReservationService {
    ReservationDTO create(ReservationDTO dto, long createdById);
    ReservationDTO update(ReservationDTO dto);
    boolean delete(long id);
    ReservationDTO findById(long id);
    ReservationDTO findByReservationNo(String reservationNo);
    List<ReservationDTO> findAll();
    List<ReservationDTO> search(String keyword);
}
