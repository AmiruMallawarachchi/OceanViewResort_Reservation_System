package com.oceanview.resort.strategyTest;

import com.oceanview.resort.model.Discount;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.enums.DiscountType;
import com.oceanview.resort.strategy.DiscountCalculationContext;
import com.oceanview.resort.strategy.impl.PromotionDiscountStrategy;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class PromotionDiscountStrategyTest {

    private PromotionDiscountStrategy strategy;
    private Reservation reservation;

    @Before
    public void setUp() {
        strategy = new PromotionDiscountStrategy();
        reservation = new Reservation();
    }

    @Test
    public void testCalculateDiscount_withNullContext_returnsZero() {
        List<Discount> discounts = Collections.singletonList(createPromotionDiscount(1L, "10"));
        assertEquals(BigDecimal.ZERO, strategy.calculateDiscount(reservation, discounts, null));
    }

    @Test
    public void testCalculateDiscount_withNullSelectedIds_returnsZero() {
        DiscountCalculationContext context = new DiscountCalculationContext();
        context.setSelectedPromotionIds(null);
        assertEquals(BigDecimal.ZERO, strategy.calculateDiscount(reservation, Collections.emptyList(), context));
    }

    @Test
    public void testCalculateDiscount_withEmptySelectedIds_returnsZero() {
        DiscountCalculationContext context = new DiscountCalculationContext();
        context.setSelectedPromotionIds(Collections.emptyList());
        List<Discount> discounts = Collections.singletonList(createPromotionDiscount(1L, "10"));
        assertEquals(BigDecimal.ZERO, strategy.calculateDiscount(reservation, discounts, context));
    }

    @Test
    public void testCalculateDiscount_sumsSelectedPromotions() {
        Discount d1 = createPromotionDiscount(1L, "10");
        Discount d2 = createPromotionDiscount(2L, "5");
        List<Discount> discounts = Arrays.asList(d1, d2);

        DiscountCalculationContext context = new DiscountCalculationContext();
        context.setSelectedPromotionIds(Arrays.asList(1L, 2L));

        BigDecimal result = strategy.calculateDiscount(reservation, discounts, context);
        assertEquals(0, new BigDecimal("15").compareTo(result));
    }

    @Test
    public void testCalculateDiscount_ignoresNonSelectedPromotions() {
        Discount d1 = createPromotionDiscount(1L, "10");
        Discount d2 = createPromotionDiscount(2L, "5");
        List<Discount> discounts = Arrays.asList(d1, d2);

        DiscountCalculationContext context = new DiscountCalculationContext();
        context.setSelectedPromotionIds(Collections.singletonList(1L));

        BigDecimal result = strategy.calculateDiscount(reservation, discounts, context);
        assertEquals(0, new BigDecimal("10").compareTo(result));
    }

    @Test
    public void testCalculateDiscount_ignoresInactivePromotions() {
        Discount d = createPromotionDiscount(1L, "10");
        d.setActive(false);
        List<Discount> discounts = Collections.singletonList(d);

        DiscountCalculationContext context = new DiscountCalculationContext();
        context.setSelectedPromotionIds(Collections.singletonList(1L));

        assertEquals(BigDecimal.ZERO, strategy.calculateDiscount(reservation, discounts, context));
    }

    @Test
    public void testGetDiscountType() {
        assertEquals(DiscountType.PROMOTION, strategy.getDiscountType());
    }

    private static Discount createPromotionDiscount(long id, String percent) {
        Discount d = new Discount();
        d.setId(id);
        d.setDiscountType(DiscountType.PROMOTION);
        d.setPercent(new BigDecimal(percent));
        d.setActive(true);
        return d;
    }
}
