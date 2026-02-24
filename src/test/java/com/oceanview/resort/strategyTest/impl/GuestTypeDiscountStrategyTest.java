package com.oceanview.resort.strategyTest.impl;

import com.oceanview.resort.model.Discount;
import com.oceanview.resort.model.Guest;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.enums.DiscountType;
import com.oceanview.resort.model.enums.GuestType;
import com.oceanview.resort.strategy.DiscountCalculationContext;
import com.oceanview.resort.strategy.impl.GuestTypeDiscountStrategy;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class GuestTypeDiscountStrategyTest {

    private GuestTypeDiscountStrategy strategy;
    private Reservation reservation;

    @Before
    public void setUp() {
        strategy = new GuestTypeDiscountStrategy();
        reservation = new Reservation();
    }

    @Test
    public void testCalculateDiscount_withNullReservation_returnsZero() {
        assertEquals(BigDecimal.ZERO, strategy.calculateDiscount(null, Collections.emptyList(), new DiscountCalculationContext()));
    }

    @Test
    public void testCalculateDiscount_withNullGuest_returnsZero() {
        reservation.setGuest(null);
        List<Discount> discounts = Collections.singletonList(createGuestTypeDiscount(GuestType.VIP, "15"));
        assertEquals(BigDecimal.ZERO, strategy.calculateDiscount(reservation, discounts, new DiscountCalculationContext()));
    }

    @Test
    public void testCalculateDiscount_withNullGuestType_returnsZero() {
        Guest guest = new Guest();
        guest.setGuestType(null);
        reservation.setGuest(guest);
        List<Discount> discounts = Collections.singletonList(createGuestTypeDiscount(GuestType.VIP, "15"));
        assertEquals(BigDecimal.ZERO, strategy.calculateDiscount(reservation, discounts, new DiscountCalculationContext()));
    }

    @Test
    public void testCalculateDiscount_returnsMaxDiscountForGuestType() {
        Guest guest = new Guest();
        guest.setGuestType(GuestType.VIP);
        reservation.setGuest(guest);

        Discount d1 = createGuestTypeDiscount(GuestType.VIP, "10");
        Discount d2 = createGuestTypeDiscount(GuestType.VIP, "15");
        List<Discount> discounts = Arrays.asList(d1, d2);

        BigDecimal result = strategy.calculateDiscount(reservation, discounts, new DiscountCalculationContext());
        assertEquals(0, new BigDecimal("15").compareTo(result));
    }

    @Test
    public void testCalculateDiscount_ignoresOtherGuestTypes() {
        Guest guest = new Guest();
        guest.setGuestType(GuestType.VIP);
        reservation.setGuest(guest);

        Discount vipDiscount = createGuestTypeDiscount(GuestType.VIP, "12");
        Discount regularDiscount = createGuestTypeDiscount(GuestType.REGULAR, "5");
        List<Discount> discounts = Arrays.asList(vipDiscount, regularDiscount);

        BigDecimal result = strategy.calculateDiscount(reservation, discounts, new DiscountCalculationContext());
        assertEquals(0, new BigDecimal("12").compareTo(result));
    }

    @Test
    public void testCalculateDiscount_ignoresInactiveDiscounts() {
        Guest guest = new Guest();
        guest.setGuestType(GuestType.VIP);
        reservation.setGuest(guest);

        Discount d = createGuestTypeDiscount(GuestType.VIP, "20");
        d.setActive(false);
        List<Discount> discounts = Collections.singletonList(d);

        assertEquals(BigDecimal.ZERO, strategy.calculateDiscount(reservation, discounts, new DiscountCalculationContext()));
    }

    @Test
    public void testGetDiscountType() {
        assertEquals(DiscountType.GUEST_TYPE, strategy.getDiscountType());
    }

    private static Discount createGuestTypeDiscount(GuestType guestType, String percent) {
        Discount d = new Discount();
        d.setId(1L);
        d.setDiscountType(DiscountType.GUEST_TYPE);
        d.setGuestType(guestType);
        d.setPercent(new BigDecimal(percent));
        d.setActive(true);
        return d;
    }
}
