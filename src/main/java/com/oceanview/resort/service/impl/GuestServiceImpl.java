package com.oceanview.resort.service.impl;

import com.oceanview.resort.dto.GuestDTO;
import com.oceanview.resort.mapper.GuestMapper;
import com.oceanview.resort.model.Guest;
import com.oceanview.resort.repository.GuestRepository;
import com.oceanview.resort.service.GuestService;
import com.oceanview.resort.security.ValidationUtil;

import java.util.List;
import java.util.stream.Collectors;

public class GuestServiceImpl implements GuestService {
    private final GuestRepository guestRepository;

    public GuestServiceImpl(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    @Override
    public GuestDTO createGuest(GuestDTO dto) {
        ValidationUtil.requireNonBlank(dto.getFullName(), "Guest name is required");
        Guest guest = GuestMapper.toEntity(dto);
        return GuestMapper.toDTO(guestRepository.create(guest));
    }

    @Override
    public GuestDTO updateGuest(GuestDTO dto) {
        Guest guest = GuestMapper.toEntity(dto);
        return GuestMapper.toDTO(guestRepository.update(guest));
    }

    @Override
    public boolean deleteGuest(long id) {
        return guestRepository.delete(id);
    }

    @Override
    public GuestDTO findById(long id) {
        return GuestMapper.toDTO(guestRepository.findById(id));
    }

    @Override
    public GuestDTO findByIdNumber(String idNumber) {
        return GuestMapper.toDTO(guestRepository.findByIdNumber(idNumber));
    }

    @Override
    public List<GuestDTO> findAll() {
        return guestRepository.findAll().stream().map(GuestMapper::toDTO).collect(Collectors.toList());
    }
}
