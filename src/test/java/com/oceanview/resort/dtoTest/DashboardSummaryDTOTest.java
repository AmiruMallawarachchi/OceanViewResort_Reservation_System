package com.oceanview.resort.dtoTest;

import com.oceanview.resort.dto.DashboardSummaryDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * Tests for DashboardSummaryDTO getters and setters.
 */
public class DashboardSummaryDTOTest {

    private DashboardSummaryDTO dto;

    @Before
    public void setup() {
        dto = new DashboardSummaryDTO();
    }

    @Test
    public void testDefaultValues() {
        Assert.assertEquals(0L, dto.getTotalRooms());
        Assert.assertEquals(0L, dto.getAvailableRooms());
        Assert.assertEquals(0L, dto.getOccupiedRooms());
        Assert.assertEquals(0.0, dto.getOccupancyRate(), 0.0);
        Assert.assertEquals(0L, dto.getTotalReservations());
        Assert.assertEquals(0L, dto.getCancelledReservations());
        Assert.assertEquals(0.0, dto.getCancellationRate(), 0.0);
        Assert.assertNull(dto.getTotalRevenue());
        Assert.assertNull(dto.getTotalDiscounts());
    }

    @Test
    public void testSetAndGetAllFields() {
        dto.setTotalRooms(50L);
        dto.setAvailableRooms(30L);
        dto.setOccupiedRooms(20L);
        dto.setOccupancyRate(40.5);
        dto.setTotalReservations(100L);
        dto.setCancelledReservations(5L);
        dto.setCancellationRate(5.0);
        dto.setTotalRevenue(new BigDecimal("1500000.00"));
        dto.setTotalDiscounts(new BigDecimal("50000.00"));

        Assert.assertEquals(50L, dto.getTotalRooms());
        Assert.assertEquals(30L, dto.getAvailableRooms());
        Assert.assertEquals(20L, dto.getOccupiedRooms());
        Assert.assertEquals(40.5, dto.getOccupancyRate(), 0.0);
        Assert.assertEquals(100L, dto.getTotalReservations());
        Assert.assertEquals(5L, dto.getCancelledReservations());
        Assert.assertEquals(5.0, dto.getCancellationRate(), 0.0);
        Assert.assertEquals(0, new BigDecimal("1500000.00").compareTo(dto.getTotalRevenue()));
        Assert.assertEquals(0, new BigDecimal("50000.00").compareTo(dto.getTotalDiscounts()));
    }
}
