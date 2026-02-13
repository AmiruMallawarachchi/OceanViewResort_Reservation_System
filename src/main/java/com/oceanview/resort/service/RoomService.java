package com.oceanview.resort.service;

import com.oceanview.resort.dto.RoomDTO;

import java.time.LocalDate;
import java.util.List;

public interface RoomService {
    RoomDTO create(RoomDTO dto);
    RoomDTO update(RoomDTO dto);
    boolean delete(long id);
    RoomDTO findById(long id);
    List<RoomDTO> findAll();
    List<RoomDTO> findAvailable(LocalDate checkIn, LocalDate checkOut);
}
