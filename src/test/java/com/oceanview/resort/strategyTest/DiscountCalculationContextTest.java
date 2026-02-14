package com.oceanview.resort.strategyTest;

import com.oceanview.resort.strategy.DiscountCalculationContext;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class DiscountCalculationContextTest {

    private DiscountCalculationContext context;

    @Before
    public void setUp() {
        context = new DiscountCalculationContext();
    }

    @Test
    public void testDefaultConstructor() {
        assertNull(context.getSelectedPromotionIds());
        assertNull(context.getManualDiscountPercent());
    }

    @Test
    public void testFullConstructor() {
        List<Long> promotionIds = Arrays.asList(1L, 2L);
        BigDecimal manualPercent = new BigDecimal("15.50");
        context = new DiscountCalculationContext(promotionIds, manualPercent);

        assertEquals(promotionIds, context.getSelectedPromotionIds());
        assertEquals(0, manualPercent.compareTo(context.getManualDiscountPercent()));
    }

    @Test
    public void testSetSelectedPromotionIds() {
        List<Long> ids = Arrays.asList(10L, 20L);
        context.setSelectedPromotionIds(ids);
        assertEquals(ids, context.getSelectedPromotionIds());
    }

    @Test
    public void testSetEmptyPromotionIds() {
        context.setSelectedPromotionIds(Collections.emptyList());
        assertNotNull(context.getSelectedPromotionIds());
        assertTrue(context.getSelectedPromotionIds().isEmpty());
    }

    @Test
    public void testSetManualDiscountPercent() {
        BigDecimal percent = new BigDecimal("25.00");
        context.setManualDiscountPercent(percent);
        assertEquals(0, percent.compareTo(context.getManualDiscountPercent()));
    }

    @Test
    public void testSetManualDiscountPercentZero() {
        context.setManualDiscountPercent(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, context.getManualDiscountPercent());
    }
}
