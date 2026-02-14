package com.oceanview.resort.service.impl;

import com.oceanview.resort.dto.DiscountDTO;
import com.oceanview.resort.mapper.DiscountMapper;
import com.oceanview.resort.model.Discount;
import com.oceanview.resort.repository.DiscountRepository;
import com.oceanview.resort.service.DiscountService;

import java.util.List;
import java.util.stream.Collectors;

public class DiscountServiceImpl implements DiscountService {
    private final DiscountRepository repository;

    public DiscountServiceImpl(DiscountRepository repository) {
        this.repository = repository;
    }

    @Override
    public DiscountDTO create(DiscountDTO dto) {
        Discount discount = DiscountMapper.toEntity(dto);
        return DiscountMapper.toDTO(repository.create(discount));
    }

    @Override
    public DiscountDTO update(DiscountDTO dto) {
        Discount discount = DiscountMapper.toEntity(dto);
        return DiscountMapper.toDTO(repository.update(discount));
    }

    @Override
    public boolean delete(long id) {
        return repository.delete(id);
    }

    @Override
    public DiscountDTO findById(long id) {
        return DiscountMapper.toDTO(repository.findById(id));
    }

    @Override
    public List<DiscountDTO> findAll() {
        return repository.findAll().stream().map(DiscountMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<DiscountDTO> findActive() {
        return repository.findActive().stream().map(DiscountMapper::toDTO).collect(Collectors.toList());
    }
}
