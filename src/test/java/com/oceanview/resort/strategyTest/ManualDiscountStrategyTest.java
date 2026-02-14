package com.oceanview.resort.strategyTest;

import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.strategy.DiscountCalculationContext;
import com.oceanview.resort.strategy.impl.ManualDiscountStrategy;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class ManualDiscountStrategyTest {

    private ManualDiscountStrategy strategy;
    private Reservation reservation;

    @Before
    public void setUp() {
        strategy = new ManualDiscountStrategy();
        reservation = new Reservation();
    }

    @Test
    public void testCalculateDiscount_withNullContext_returnsZero() {
        BigDecimal result = strategy.calculateDiscount(reservation, Collections.emptyList(), null);
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    public void testCalculateDiscount_withNullManualPercent_returnsZero() {
        DiscountCalculationContext context = new DiscountCalculationContext();
        context.setManualDiscountPercent(null);
        BigDecimal result = strategy.calculateDiscount(reservation, Collections.emptyList(), context);
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    public void testCalculateDiscount_withValidManualPercent_returnsPercent() {
        DiscountCalculationContext context = new DiscountCalculationContext();
        context.setManualDiscountPercent(new BigDecimal("20.00"));
        BigDecimal result = strategy.calculateDiscount(reservation, Collections.emptyList(), context);
        assertEquals(0, new BigDecimal("20.00").compareTo(result));
    }

    @Test
    public void testCalculateDiscount_withNegativeManualPercent_returnsZero() {
        DiscountCalculationContext context = new DiscountCalculationContext();
        context.setManualDiscountPercent(new BigDecimal("-10"));
        BigDecimal result = strategy.calculateDiscount(reservation, Collections.emptyList(), context);
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    public void testGetDiscountType_returnsNull() {
        assertNull(strategy.getDiscountType());
    }

    @Test
    public void testGetStrategyName() {
        assertEquals("ManualDiscountStrategy", strategy.getStrategyName());
    }
}
