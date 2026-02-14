package com.oceanview.resort.service;

import com.oceanview.resort.dto.DiscountDTO;

import java.util.List;

public interface DiscountService {
    DiscountDTO create(DiscountDTO dto);
    DiscountDTO update(DiscountDTO dto);
    boolean delete(long id);
    DiscountDTO findById(long id);
    List<DiscountDTO> findAll();
    List<DiscountDTO> findActive();
}
