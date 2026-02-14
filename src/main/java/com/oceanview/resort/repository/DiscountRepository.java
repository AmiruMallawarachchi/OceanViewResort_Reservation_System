package com.oceanview.resort.repository;

import com.oceanview.resort.model.Discount;

import java.util.List;

public interface DiscountRepository {
    Discount create(Discount discount);
    Discount update(Discount discount);
    boolean delete(long id);
    Discount findById(long id);
    List<Discount> findAll();
    List<Discount> findActive();
}
