package com.oceanview.resort.dtoTest;

import com.oceanview.resort.dto.RoomDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for RoomDTO getters and setters.
 */
public class RoomDTOTest {

    private RoomDTO dto;

    @Before
    public void setup() {
        dto = new RoomDTO();
    }

    @Test
    public void testDefaultValues() {
        Assert.assertEquals(0L, dto.getId());
        Assert.assertEquals(0L, dto.getRoomTypeId());
        Assert.assertEquals(0, dto.getFloor());
        Assert.assertEquals(0, dto.getRoomTypeMaxOccupancy());
        Assert.assertFalse(dto.isFullAccess());
        Assert.assertNull(dto.getRoomNumber());
        Assert.assertNull(dto.getRoomTypeName());
        Assert.assertNull(dto.getRoomTypeRatePerNight());
        Assert.assertNull(dto.getRoomTypeAmenities());
        Assert.assertNull(dto.getStatus());
        Assert.assertNull(dto.getDescription());
    }

    @Test
    public void testSetAndGetAllFields() {
        dto.setId(1L);
        dto.setRoomNumber("201");
        dto.setRoomTypeId(2L);
        dto.setRoomTypeName("Deluxe");
        dto.setRoomTypeRatePerNight("15000.00");
        dto.setRoomTypeMaxOccupancy(4);
        dto.setRoomTypeAmenities("Sea view, Mini bar");
        dto.setFloor(2);
        dto.setStatus("AVAILABLE");
        dto.setDescription("Ocean view");
        dto.setFullAccess(true);

        Assert.assertEquals(1L, dto.getId());
        Assert.assertEquals("201", dto.getRoomNumber());
        Assert.assertEquals(2L, dto.getRoomTypeId());
        Assert.assertEquals("Deluxe", dto.getRoomTypeName());
        Assert.assertEquals("15000.00", dto.getRoomTypeRatePerNight());
        Assert.assertEquals(4, dto.getRoomTypeMaxOccupancy());
        Assert.assertEquals("Sea view, Mini bar", dto.getRoomTypeAmenities());
        Assert.assertEquals(2, dto.getFloor());
        Assert.assertEquals("AVAILABLE", dto.getStatus());
        Assert.assertEquals("Ocean view", dto.getDescription());
        Assert.assertTrue(dto.isFullAccess());
    }

    @Test
    public void testSetFullAccessFalse() {
        dto.setFullAccess(false);
        Assert.assertFalse(dto.isFullAccess());
    }
}
