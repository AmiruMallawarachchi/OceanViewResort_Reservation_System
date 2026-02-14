package com.oceanview.resort.modelTest;

import com.oceanview.resort.model.Bill;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BillTest {

    private Bill bill;

    @Before
    public void setUp() {
        bill = new Bill();
    }

    @Test
    public void testDefaultValues() {
        Assert.assertEquals(0L, bill.getId());
        Assert.assertNull(bill.getBillNo());
        Assert.assertNull(bill.getReservation());
        Assert.assertEquals(0, bill.getNumberOfNights());
        Assert.assertNull(bill.getRoomRate());
        Assert.assertNull(bill.getTotalAmount());
        Assert.assertNull(bill.getDiscountAmount());
        Assert.assertNull(bill.getTaxAmount());
        Assert.assertNull(bill.getNetAmount());
        Assert.assertNull(bill.getGeneratedBy());
        Assert.assertNull(bill.getGeneratedAt());
    }

    @Test
    public void testGettersAndSetters() {
        Reservation res = new Reservation();
        User user = new User();
        LocalDateTime at = LocalDateTime.of(2026, 2, 12, 10, 0);

        bill.setId(1L);
        bill.setBillNo("BILL-001");
        bill.setReservation(res);
        bill.setNumberOfNights(3);
        bill.setRoomRate(new BigDecimal("15000.00"));
        bill.setTotalAmount(new BigDecimal("45000.00"));
        bill.setDiscountAmount(new BigDecimal("2000.00"));
        bill.setTaxAmount(new BigDecimal("4300.00"));
        bill.setNetAmount(new BigDecimal("47300.00"));
        bill.setGeneratedBy(user);
        bill.setGeneratedAt(at);

        Assert.assertEquals(1L, bill.getId());
        Assert.assertEquals("BILL-001", bill.getBillNo());
        Assert.assertEquals(res, bill.getReservation());
        Assert.assertEquals(3, bill.getNumberOfNights());
        Assert.assertEquals(0, new BigDecimal("15000.00").compareTo(bill.getRoomRate()));
        Assert.assertEquals(0, new BigDecimal("45000.00").compareTo(bill.getTotalAmount()));
        Assert.assertEquals(0, new BigDecimal("2000.00").compareTo(bill.getDiscountAmount()));
        Assert.assertEquals(0, new BigDecimal("4300.00").compareTo(bill.getTaxAmount()));
        Assert.assertEquals(0, new BigDecimal("47300.00").compareTo(bill.getNetAmount()));
        Assert.assertEquals(user, bill.getGeneratedBy());
        Assert.assertEquals(at, bill.getGeneratedAt());
    }

    @Test
    public void testConstructorWithArgs() {
        Reservation res = new Reservation();
        User user = new User();
        LocalDateTime at = LocalDateTime.now();
        Bill b = new Bill(2L, "BILL-002", res, 2, new BigDecimal("100"), new BigDecimal("200"),
                new BigDecimal("10"), new BigDecimal("19"), new BigDecimal("209"), user, at);

        Assert.assertEquals(2L, b.getId());
        Assert.assertEquals("BILL-002", b.getBillNo());
        Assert.assertEquals(2, b.getNumberOfNights());
        Assert.assertEquals(0, new BigDecimal("100").compareTo(b.getRoomRate()));
        Assert.assertEquals(at, b.getGeneratedAt());
    }
}
