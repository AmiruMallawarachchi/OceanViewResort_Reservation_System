package com.oceanview.resort.service.impl;

import com.oceanview.resort.dto.RoomTypeDTO;
import com.oceanview.resort.mapper.RoomTypeMapper;
import com.oceanview.resort.model.RoomType;
import com.oceanview.resort.repository.RoomRepository;
import com.oceanview.resort.repository.RoomTypeRepository;
import com.oceanview.resort.service.RoomTypeService;
import com.oceanview.resort.security.ValidationUtil;

import java.util.List;
import java.util.stream.Collectors;

public class RoomTypeServiceImpl implements RoomTypeService {
    private final RoomTypeRepository repository;
    private final RoomRepository roomRepository;

    public RoomTypeServiceImpl(RoomTypeRepository repository) {
        this(repository, null);
    }

    public RoomTypeServiceImpl(RoomTypeRepository repository, RoomRepository roomRepository) {
        this.repository = repository;
        this.roomRepository = roomRepository;
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
        if (roomRepository != null && roomRepository.countByRoomTypeId(id) > 0) {
            throw new IllegalArgumentException("Cannot delete room type: it has associated rooms. Remove or reassign the rooms first.");
        }
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
