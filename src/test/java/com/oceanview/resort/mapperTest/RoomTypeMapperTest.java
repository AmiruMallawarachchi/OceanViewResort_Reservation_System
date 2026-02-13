package com.oceanview.resort.mapperTest;

import com.oceanview.resort.dto.RoomTypeDTO;
import com.oceanview.resort.mapper.RoomTypeMapper;
import com.oceanview.resort.model.RoomType;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * Tests for RoomTypeMapper (RoomType <-> RoomTypeDTO).
 */
public class RoomTypeMapperTest {

    @Test
    public void toDTO_nullReturnsNull() {
        Assert.assertNull(RoomTypeMapper.toDTO(null));
    }

    @Test
    public void toDTO_mapsAllFields() {
        RoomType roomType = new RoomType();
        roomType.setId(1L);
        roomType.setTypeName("Standard");
        roomType.setDescription("Standard room");
        roomType.setRatePerNight(new BigDecimal("10000.00"));
        roomType.setMaxOccupancy(2);
        roomType.setAmenities("WiFi, AC");
        roomType.setActive(true);

        RoomTypeDTO dto = RoomTypeMapper.toDTO(roomType);

        Assert.assertNotNull(dto);
        Assert.assertEquals(1L, dto.getId());
        Assert.assertEquals("Standard", dto.getTypeName());
        Assert.assertEquals("Standard room", dto.getDescription());
        Assert.assertEquals("10000.00", dto.getRatePerNight());
        Assert.assertEquals(2, dto.getMaxOccupancy());
        Assert.assertEquals("WiFi, AC", dto.getAmenities());
        Assert.assertTrue(dto.isActive());
    }

    @Test
    public void toDTO_nullRatePerNight_mapsToNull() {
        RoomType roomType = new RoomType();
        roomType.setId(2L);
        roomType.setTypeName("Test");
        roomType.setRatePerNight(null);

        RoomTypeDTO dto = RoomTypeMapper.toDTO(roomType);

        Assert.assertNull(dto.getRatePerNight());
    }

    @Test
    public void toEntity_nullReturnsNull() {
        Assert.assertNull(RoomTypeMapper.toEntity(null));
    }

    @Test
    public void toEntity_mapsAllFields() {
        RoomTypeDTO dto = new RoomTypeDTO();
        dto.setId(1L);
        dto.setTypeName("Deluxe");
        dto.setDescription("Deluxe room");
        dto.setRatePerNight("15000.00");
        dto.setMaxOccupancy(4);
        dto.setAmenities("Sea view");
        dto.setActive(false);

        RoomType roomType = RoomTypeMapper.toEntity(dto);

        Assert.assertNotNull(roomType);
        Assert.assertEquals(1L, roomType.getId());
        Assert.assertEquals("Deluxe", roomType.getTypeName());
        Assert.assertEquals("Deluxe room", roomType.getDescription());
        Assert.assertEquals(0, new BigDecimal("15000.00").compareTo(roomType.getRatePerNight()));
        Assert.assertEquals(4, roomType.getMaxOccupancy());
        Assert.assertEquals("Sea view", roomType.getAmenities());
        Assert.assertFalse(roomType.isActive());
    }

    @Test
    public void toEntity_nullRatePerNight_leavesNull() {
        RoomTypeDTO dto = new RoomTypeDTO();
        dto.setId(2L);
        dto.setTypeName("Test");
        dto.setRatePerNight(null);

        RoomType roomType = RoomTypeMapper.toEntity(dto);

        Assert.assertNull(roomType.getRatePerNight());
    }

    @Test
    public void toEntity_blankRatePerNight_leavesNull() {
        RoomTypeDTO dto = new RoomTypeDTO();
        dto.setId(3L);
        dto.setTypeName("Test");
        dto.setRatePerNight("   ");

        RoomType roomType = RoomTypeMapper.toEntity(dto);

        Assert.assertNull(roomType.getRatePerNight());
    }
}
