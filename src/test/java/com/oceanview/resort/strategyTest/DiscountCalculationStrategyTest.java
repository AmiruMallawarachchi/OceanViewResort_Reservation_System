package com.oceanview.resort.strategyTest;

import com.oceanview.resort.model.Discount;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.enums.DiscountType;
import com.oceanview.resort.strategy.DiscountCalculationContext;
import com.oceanview.resort.strategy.DiscountCalculationStrategy;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class DiscountCalculationStrategyTest {

    private static final class TestStrategy implements DiscountCalculationStrategy {
        @Override
        public BigDecimal calculateDiscount(Reservation reservation, List<Discount> activeDiscounts, DiscountCalculationContext context) {
            return BigDecimal.ZERO;
        }

        @Override
        public DiscountType getDiscountType() {
            return DiscountType.PROMOTION;
        }
    }

    @Test
    public void getStrategyName_defaultUsesSimpleClassName() {
        DiscountCalculationStrategy strategy = new TestStrategy();
        Assert.assertEquals("TestStrategy", strategy.getStrategyName());
    }

    @Test
    public void interfaceMethods_canBeCalled() {
        DiscountCalculationStrategy strategy = new TestStrategy();
        Assert.assertEquals(DiscountType.PROMOTION, strategy.getDiscountType());
        Assert.assertEquals(BigDecimal.ZERO, strategy.calculateDiscount(new Reservation(), Collections.emptyList(), new DiscountCalculationContext()));
    }
}

