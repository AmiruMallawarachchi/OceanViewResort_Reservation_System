package com.oceanview.resort.dtoTest;

import com.oceanview.resort.dto.RoomTypeDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for RoomTypeDTO getters and setters.
 */
public class RoomTypeDTOTest {

    private RoomTypeDTO dto;

    @Before
    public void setup() {
        dto = new RoomTypeDTO();
    }

    @Test
    public void testDefaultValues() {
        Assert.assertEquals(0L, dto.getId());
        Assert.assertEquals(0, dto.getMaxOccupancy());
        Assert.assertFalse(dto.isActive());
        Assert.assertNull(dto.getTypeName());
        Assert.assertNull(dto.getDescription());
        Assert.assertNull(dto.getRatePerNight());
        Assert.assertNull(dto.getAmenities());
    }

    @Test
    public void testSetAndGetAllFields() {
        dto.setId(1L);
        dto.setTypeName("Standard");
        dto.setDescription("Standard room");
        dto.setRatePerNight("10000.00");
        dto.setMaxOccupancy(2);
        dto.setAmenities("WiFi, AC");
        dto.setActive(true);

        Assert.assertEquals(1L, dto.getId());
        Assert.assertEquals("Standard", dto.getTypeName());
        Assert.assertEquals("Standard room", dto.getDescription());
        Assert.assertEquals("10000.00", dto.getRatePerNight());
        Assert.assertEquals(2, dto.getMaxOccupancy());
        Assert.assertEquals("WiFi, AC", dto.getAmenities());
        Assert.assertTrue(dto.isActive());
    }

    @Test
    public void testSetActiveFalse() {
        dto.setActive(false);
        Assert.assertFalse(dto.isActive());
    }
}
