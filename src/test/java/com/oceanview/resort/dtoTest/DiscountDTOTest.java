package com.oceanview.resort.dtoTest;

import com.oceanview.resort.dto.DiscountDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for DiscountDTO getters and setters.
 */
public class DiscountDTOTest {

    private DiscountDTO dto;

    @Before
    public void setup() {
        dto = new DiscountDTO();
    }

    @Test
    public void testDefaultValues() {
        Assert.assertEquals(0L, dto.getId());
        Assert.assertFalse(dto.isActive());
        Assert.assertNull(dto.getName());
        Assert.assertNull(dto.getDiscountType());
        Assert.assertNull(dto.getGuestType());
        Assert.assertNull(dto.getPercent());
        Assert.assertNull(dto.getDescription());
    }

    @Test
    public void testSetAndGetAllFields() {
        dto.setId(1L);
        dto.setName("Summer Sale");
        dto.setDiscountType("PROMOTION");
        dto.setGuestType("VIP");
        dto.setPercent("15");
        dto.setDescription("Summer discount");
        dto.setActive(true);

        Assert.assertEquals(1L, dto.getId());
        Assert.assertEquals("Summer Sale", dto.getName());
        Assert.assertEquals("PROMOTION", dto.getDiscountType());
        Assert.assertEquals("VIP", dto.getGuestType());
        Assert.assertEquals("15", dto.getPercent());
        Assert.assertEquals("Summer discount", dto.getDescription());
        Assert.assertTrue(dto.isActive());
    }

    @Test
    public void testSetActiveFalse() {
        dto.setActive(false);
        Assert.assertFalse(dto.isActive());
    }
}
