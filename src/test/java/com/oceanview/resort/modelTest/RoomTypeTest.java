package com.oceanview.resort.modelTest;

import com.oceanview.resort.model.RoomType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

public class RoomTypeTest {

    private RoomType roomType;

    @Before
    public void setUp() {
        roomType = new RoomType();
    }

    @Test
    public void testDefaultValues() {
        Assert.assertEquals(0L, roomType.getId());
        Assert.assertNull(roomType.getTypeName());
        Assert.assertNull(roomType.getDescription());
        Assert.assertNull(roomType.getRatePerNight());
        Assert.assertEquals(0, roomType.getMaxOccupancy());
        Assert.assertNull(roomType.getAmenities());
        Assert.assertFalse(roomType.isActive());
    }

    @Test
    public void testGettersAndSetters() {
        roomType.setId(1L);
        roomType.setTypeName("Deluxe");
        roomType.setDescription("Deluxe ocean view");
        roomType.setRatePerNight(new BigDecimal("15000.00"));
        roomType.setMaxOccupancy(4);
        roomType.setAmenities("WiFi, Mini bar");
        roomType.setActive(true);

        Assert.assertEquals(1L, roomType.getId());
        Assert.assertEquals("Deluxe", roomType.getTypeName());
        Assert.assertEquals("Deluxe ocean view", roomType.getDescription());
        Assert.assertEquals(0, new BigDecimal("15000.00").compareTo(roomType.getRatePerNight()));
        Assert.assertEquals(4, roomType.getMaxOccupancy());
        Assert.assertEquals("WiFi, Mini bar", roomType.getAmenities());
        Assert.assertTrue(roomType.isActive());
    }

    @Test
    public void testConstructorWithArgs() {
        RoomType rt = new RoomType(2L, "Standard", "Standard room", new BigDecimal("10000"), 2, "WiFi", false);
        Assert.assertEquals(2L, rt.getId());
        Assert.assertEquals("Standard", rt.getTypeName());
        Assert.assertEquals(2, rt.getMaxOccupancy());
        Assert.assertFalse(rt.isActive());
    }
}
