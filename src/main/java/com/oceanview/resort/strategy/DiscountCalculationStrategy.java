package com.oceanview.resort.strategy;

import com.oceanview.resort.model.Discount;
import com.oceanview.resort.model.Reservation;

import java.math.BigDecimal;
import java.util.List;

/**
 * Strategy interface for discount calculation algorithms.
 * The Strategy Pattern allows different discount calculation methods to be
 * encapsulated and made interchangeable. This makes it easy to add new discount
 */
public interface DiscountCalculationStrategy {

    BigDecimal calculateDiscount(Reservation reservation, List<Discount> activeDiscounts, DiscountCalculationContext context);

    com.oceanview.resort.model.enums.DiscountType getDiscountType();

    default String getStrategyName() {
        return this.getClass().getSimpleName();
    }
}
