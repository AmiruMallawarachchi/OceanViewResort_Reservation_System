package com.oceanview.resort.dtoTest;

import com.oceanview.resort.dto.ReservationDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for ReservationDTO (getters, setters, and default values).
 */
public class ReservationDTOTest {

    private ReservationDTO dto;

    @Before
    public void setup() {
        dto = new ReservationDTO();
    }

    @Test
    public void testDefaultValues() {
        Assert.assertEquals(0L, dto.getId());
        Assert.assertEquals(0L, dto.getGuestId());
        Assert.assertEquals(0L, dto.getRoomId());
        Assert.assertNull(dto.getReservationNo());
        Assert.assertNull(dto.getGuestName());
        Assert.assertNull(dto.getGuestType());
        Assert.assertNull(dto.getRoomNumber());
        Assert.assertNull(dto.getRoomTypeName());
        Assert.assertNull(dto.getCheckInDate());
        Assert.assertNull(dto.getCheckOutDate());
        Assert.assertNull(dto.getStatus());
        Assert.assertNull(dto.getCreatedBy());
        Assert.assertNull(dto.getCreatedAt());
    }

    @Test
    public void testSetAndGetCoreFields() {
        dto.setId(1L);
        dto.setReservationNo("RES-1-101-20260212");
        dto.setGuestId(10L);
        dto.setGuestName("Amaya Perera");
        dto.setGuestType("REGULAR");
        dto.setRoomId(20L);
        dto.setRoomNumber("101");
        dto.setRoomTypeName("Deluxe");
        dto.setCheckInDate("2026-02-12");
        dto.setCheckOutDate("2026-02-13");
        dto.setStatus("PENDING");
        dto.setCreatedBy("reservationist");
        dto.setCreatedAt("2026-02-10 12:00");

        Assert.assertEquals(1L, dto.getId());
        Assert.assertEquals("RES-1-101-20260212", dto.getReservationNo());
        Assert.assertEquals(10L, dto.getGuestId());
        Assert.assertEquals("Amaya Perera", dto.getGuestName());
        Assert.assertEquals("REGULAR", dto.getGuestType());
        Assert.assertEquals(20L, dto.getRoomId());
        Assert.assertEquals("101", dto.getRoomNumber());
        Assert.assertEquals("Deluxe", dto.getRoomTypeName());
        Assert.assertEquals("2026-02-12", dto.getCheckInDate());
        Assert.assertEquals("2026-02-13", dto.getCheckOutDate());
        Assert.assertEquals("PENDING", dto.getStatus());
        Assert.assertEquals("reservationist", dto.getCreatedBy());
        Assert.assertEquals("2026-02-10 12:00", dto.getCreatedAt());
    }

    @Test
    public void testUpdateFields() {
        dto.setId(1L);
        dto.setReservationNo("OLD");
        dto.setGuestId(1L);
        dto.setRoomId(1L);
        dto.setStatus("PENDING");

        dto.setId(2L);
        dto.setReservationNo("NEW");
        dto.setGuestId(3L);
        dto.setRoomId(4L);
        dto.setStatus("CONFIRMED");

        Assert.assertEquals(2L, dto.getId());
        Assert.assertEquals("NEW", dto.getReservationNo());
        Assert.assertEquals(3L, dto.getGuestId());
        Assert.assertEquals(4L, dto.getRoomId());
        Assert.assertEquals("CONFIRMED", dto.getStatus());
    }
}

