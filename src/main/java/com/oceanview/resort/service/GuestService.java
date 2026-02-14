package com.oceanview.resort.service;

import com.oceanview.resort.dto.GuestDTO;

import java.util.List;

public interface GuestService {
    GuestDTO createGuest(GuestDTO dto);
    GuestDTO updateGuest(GuestDTO dto);
    boolean deleteGuest(long id);
    GuestDTO findById(long id);
    GuestDTO findByIdNumber(String idNumber);
    List<GuestDTO> findAll();
}
