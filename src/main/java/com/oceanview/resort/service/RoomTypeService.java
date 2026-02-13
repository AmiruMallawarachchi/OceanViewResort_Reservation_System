package com.oceanview.resort.service;

import com.oceanview.resort.dto.RoomTypeDTO;

import java.util.List;

public interface RoomTypeService {
    RoomTypeDTO create(RoomTypeDTO dto);
    RoomTypeDTO update(RoomTypeDTO dto);
    boolean delete(long id);
    RoomTypeDTO findById(long id);
    List<RoomTypeDTO> findAll();
}
