package com.oceanview.resort.strategyTest;

import com.oceanview.resort.model.Guest;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.repository.DiscountRepository;
import com.oceanview.resort.strategy.DiscountCalculationContext;
import com.oceanview.resort.strategy.DiscountCalculationManager;
import com.oceanview.resort.strategy.DiscountCalculationStrategy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DiscountCalculationManagerTest {

    @Mock
    private DiscountRepository discountRepository;

    private DiscountCalculationManager manager;
    private Reservation reservation;

    @Before
    public void setUp() {
        when(discountRepository.findActive()).thenReturn(Collections.emptyList());
        List<DiscountCalculationStrategy> strategies = List.of(
            new com.oceanview.resort.strategy.impl.ManualDiscountStrategy(),
            new com.oceanview.resort.strategy.impl.GuestTypeDiscountStrategy()
        );
        manager = new DiscountCalculationManager(strategies, discountRepository);
        reservation = new Reservation();
    }

    @Test
    public void testCalculateTotalDiscount_nullReservation_returnsZero() {
        BigDecimal result = manager.calculateTotalDiscount(null, new DiscountCalculationContext());
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    public void testCalculateTotalDiscount_noStrategiesApply_returnsZero() {
        reservation.setGuest(new Guest());
        reservation.getGuest().setGuestType(null);
        DiscountCalculationContext context = new DiscountCalculationContext();
        BigDecimal result = manager.calculateTotalDiscount(reservation, context);
        assertEquals(0, result.compareTo(BigDecimal.ZERO));
    }

    @Test
    public void testCalculateTotalDiscount_withManualContext_returnsManualPercent() {
        DiscountCalculationContext context = new DiscountCalculationContext();
        context.setManualDiscountPercent(new BigDecimal("25.00"));
        BigDecimal result = manager.calculateTotalDiscount(reservation, context);
        assertEquals(0, new BigDecimal("25.00").compareTo(result));
    }

    @Test
    public void testCalculateTotalDiscount_capsAt100() {
        when(discountRepository.findActive()).thenReturn(Collections.emptyList());
        DiscountCalculationContext context = new DiscountCalculationContext();
        context.setManualDiscountPercent(new BigDecimal("150"));
        BigDecimal result = manager.calculateTotalDiscount(reservation, context);
        assertEquals(0, BigDecimal.valueOf(100).compareTo(result));
    }

    @Test
    public void testGetStrategyCount() {
        assertEquals(2, manager.getStrategyCount());
    }

    @Test
    public void testConstructor_withNullStrategies_usesEmptyList() {
        DiscountCalculationManager m = new DiscountCalculationManager(null, discountRepository);
        assertEquals(0, m.getStrategyCount());
        assertEquals(0, m.calculateTotalDiscount(reservation, new DiscountCalculationContext()).compareTo(BigDecimal.ZERO));
    }
}
