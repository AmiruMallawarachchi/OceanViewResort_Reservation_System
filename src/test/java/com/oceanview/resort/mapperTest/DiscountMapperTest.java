package com.oceanview.resort.mapperTest;

import com.oceanview.resort.dto.DiscountDTO;
import com.oceanview.resort.mapper.DiscountMapper;
import com.oceanview.resort.model.Discount;
import com.oceanview.resort.model.enums.DiscountType;
import com.oceanview.resort.model.enums.GuestType;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * Tests for DiscountMapper (Discount <-> DiscountDTO).
 */
public class DiscountMapperTest {

    @Test
    public void toDTO_nullReturnsNull() {
        Assert.assertNull(DiscountMapper.toDTO(null));
    }

    @Test
    public void toDTO_mapsAllFields() {
        Discount discount = new Discount();
        discount.setId(1L);
        discount.setName("Summer Sale");
        discount.setDiscountType(DiscountType.PROMOTION);
        discount.setGuestType(GuestType.VIP);
        discount.setPercent(new BigDecimal("15.00"));
        discount.setDescription("Summer discount");
        discount.setActive(true);

        DiscountDTO dto = DiscountMapper.toDTO(discount);

        Assert.assertNotNull(dto);
        Assert.assertEquals(1L, dto.getId());
        Assert.assertEquals("Summer Sale", dto.getName());
        Assert.assertEquals("PROMOTION", dto.getDiscountType());
        Assert.assertEquals("VIP", dto.getGuestType());
        Assert.assertEquals("15.00", dto.getPercent());
        Assert.assertEquals("Summer discount", dto.getDescription());
        Assert.assertTrue(dto.isActive());
    }

    @Test
    public void toDTO_nullEnums_mapsToNull() {
        Discount discount = new Discount();
        discount.setId(2L);
        discount.setName("Test");
        discount.setDiscountType(null);
        discount.setGuestType(null);
        discount.setPercent(new BigDecimal("10"));

        DiscountDTO dto = DiscountMapper.toDTO(discount);

        Assert.assertNull(dto.getDiscountType());
        Assert.assertNull(dto.getGuestType());
    }

    @Test
    public void toDTO_nullPercent_mapsToNull() {
        Discount discount = new Discount();
        discount.setId(3L);
        discount.setPercent(null);

        DiscountDTO dto = DiscountMapper.toDTO(discount);

        Assert.assertNull(dto.getPercent());
    }

    @Test
    public void toEntity_nullReturnsNull() {
        Assert.assertNull(DiscountMapper.toEntity(null));
    }

    @Test
    public void toEntity_mapsAllFields() {
        DiscountDTO dto = new DiscountDTO();
        dto.setId(1L);
        dto.setName("Winter Sale");
        dto.setDiscountType("GUEST_TYPE");
        dto.setGuestType("REGULAR");
        dto.setPercent("20");
        dto.setDescription("Winter discount");
        dto.setActive(false);

        Discount discount = DiscountMapper.toEntity(dto);

        Assert.assertNotNull(discount);
        Assert.assertEquals(1L, discount.getId());
        Assert.assertEquals("Winter Sale", discount.getName());
        Assert.assertEquals(DiscountType.GUEST_TYPE, discount.getDiscountType());
        Assert.assertEquals(GuestType.REGULAR, discount.getGuestType());
        Assert.assertEquals(0, new BigDecimal("20").compareTo(discount.getPercent()));
        Assert.assertEquals("Winter discount", discount.getDescription());
        Assert.assertFalse(discount.isActive());
    }

    @Test
    public void toEntity_nullDiscountType_leavesNull() {
        DiscountDTO dto = new DiscountDTO();
        dto.setId(2L);
        dto.setName("Test");
        dto.setDiscountType(null);
        dto.setPercent("10");

        Discount discount = DiscountMapper.toEntity(dto);

        Assert.assertNull(discount.getDiscountType());
    }

    @Test
    public void toEntity_blankGuestType_leavesNull() {
        DiscountDTO dto = new DiscountDTO();
        dto.setId(3L);
        dto.setName("Test");
        dto.setGuestType("");
        dto.setPercent("10");

        Discount discount = DiscountMapper.toEntity(dto);

        Assert.assertNull(discount.getGuestType());
    }

    @Test
    public void toEntity_nullPercent_leavesNull() {
        DiscountDTO dto = new DiscountDTO();
        dto.setId(4L);
        dto.setName("Test");
        dto.setPercent(null);

        Discount discount = DiscountMapper.toEntity(dto);

        Assert.assertNull(discount.getPercent());
    }

    @Test
    public void toEntity_blankPercent_leavesNull() {
        DiscountDTO dto = new DiscountDTO();
        dto.setId(5L);
        dto.setName("Test");
        dto.setPercent("   ");

        Discount discount = DiscountMapper.toEntity(dto);

        Assert.assertNull(discount.getPercent());
    }
}
