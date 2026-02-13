package com.oceanview.resort.service.impl;

import com.oceanview.resort.dto.RoomTypeDTO;
import com.oceanview.resort.mapper.RoomTypeMapper;
import com.oceanview.resort.model.RoomType;
import com.oceanview.resort.repository.RoomTypeRepository;
import com.oceanview.resort.service.RoomTypeService;
import com.oceanview.resort.security.ValidationUtil;

import java.util.List;
import java.util.stream.Collectors;

public class RoomTypeServiceImpl implements RoomTypeService {
    private final RoomTypeRepository repository;

    public RoomTypeServiceImpl(RoomTypeRepository repository) {
        this.repository = repository;
    }

    @Override
    public RoomTypeDTO create(RoomTypeDTO dto) {
        ValidationUtil.requireNonBlank(dto.getTypeName(), "Type name is required");
        RoomType roomType = RoomTypeMapper.toEntity(dto);
        return RoomTypeMapper.toDTO(repository.create(roomType));
    }

    @Override
    public RoomTypeDTO update(RoomTypeDTO dto) {
        RoomType roomType = RoomTypeMapper.toEntity(dto);
        return RoomTypeMapper.toDTO(repository.update(roomType));
    }

    @Override
    public boolean delete(long id) {
        return repository.delete(id);
    }

    @Override
    public RoomTypeDTO findById(long id) {
        return RoomTypeMapper.toDTO(repository.findById(id));
    }

    @Override
    public List<RoomTypeDTO> findAll() {
        return repository.findAll().stream().map(RoomTypeMapper::toDTO).collect(Collectors.toList());
    }
}
