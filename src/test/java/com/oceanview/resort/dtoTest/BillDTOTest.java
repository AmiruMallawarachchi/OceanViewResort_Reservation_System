package com.oceanview.resort.dtoTest;

import com.oceanview.resort.dto.BillDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for BillDTO getters and setters.
 */
public class BillDTOTest {

    private BillDTO dto;

    @Before
    public void setup() {
        dto = new BillDTO();
    }

    @Test
    public void testDefaultValues() {
        Assert.assertEquals(0L, dto.getId());
        Assert.assertEquals(0, dto.getNumberOfNights());
        Assert.assertNull(dto.getBillNo());
        Assert.assertNull(dto.getReservationNo());
        Assert.assertNull(dto.getGuestName());
        Assert.assertNull(dto.getRoomNumber());
        Assert.assertNull(dto.getRoomRate());
        Assert.assertNull(dto.getTotalAmount());
        Assert.assertNull(dto.getDiscountAmount());
        Assert.assertNull(dto.getTaxAmount());
        Assert.assertNull(dto.getNetAmount());
        Assert.assertNull(dto.getGeneratedAt());
    }

    @Test
    public void testSetAndGetAllFields() {
        dto.setId(1L);
        dto.setBillNo("BILL-001");
        dto.setReservationNo("RES-001");
        dto.setGuestName("John Doe");
        dto.setRoomNumber("101");
        dto.setNumberOfNights(3);
        dto.setRoomRate("15000.00");
        dto.setTotalAmount("45000.00");
        dto.setDiscountAmount("2000.00");
        dto.setTaxAmount("4300.00");
        dto.setNetAmount("47300.00");
        dto.setGeneratedAt("2026-02-12 10:00");

        Assert.assertEquals(1L, dto.getId());
        Assert.assertEquals("BILL-001", dto.getBillNo());
        Assert.assertEquals("RES-001", dto.getReservationNo());
        Assert.assertEquals("John Doe", dto.getGuestName());
        Assert.assertEquals("101", dto.getRoomNumber());
        Assert.assertEquals(3, dto.getNumberOfNights());
        Assert.assertEquals("15000.00", dto.getRoomRate());
        Assert.assertEquals("45000.00", dto.getTotalAmount());
        Assert.assertEquals("2000.00", dto.getDiscountAmount());
        Assert.assertEquals("4300.00", dto.getTaxAmount());
        Assert.assertEquals("47300.00", dto.getNetAmount());
        Assert.assertEquals("2026-02-12 10:00", dto.getGeneratedAt());
    }
}
