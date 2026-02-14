package com.oceanview.resort.modelTest;

import com.oceanview.resort.model.Discount;
import com.oceanview.resort.model.enums.DiscountType;
import com.oceanview.resort.model.enums.GuestType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DiscountTest {

    private Discount discount;

    @Before
    public void setUp() {
        discount = new Discount();
    }

    @Test
    public void testDefaultValues() {
        Assert.assertEquals(0L, discount.getId());
        Assert.assertNull(discount.getName());
        Assert.assertNull(discount.getDiscountType());
        Assert.assertNull(discount.getGuestType());
        Assert.assertNull(discount.getPercent());
        Assert.assertNull(discount.getDescription());
        Assert.assertFalse(discount.isActive());
        Assert.assertNull(discount.getCreatedAt());
        Assert.assertNull(discount.getUpdatedAt());
    }

    @Test
    public void testGettersAndSetters() {
        LocalDateTime now = LocalDateTime.now();

        discount.setId(1L);
        discount.setName("Summer Sale");
        discount.setDiscountType(DiscountType.PROMOTION);
        discount.setGuestType(GuestType.VIP);
        discount.setPercent(new BigDecimal("15.00"));
        discount.setDescription("Summer discount");
        discount.setActive(true);
        discount.setCreatedAt(now);
        discount.setUpdatedAt(now);

        Assert.assertEquals(1L, discount.getId());
        Assert.assertEquals("Summer Sale", discount.getName());
        Assert.assertEquals(DiscountType.PROMOTION, discount.getDiscountType());
        Assert.assertEquals(GuestType.VIP, discount.getGuestType());
        Assert.assertEquals(0, new BigDecimal("15.00").compareTo(discount.getPercent()));
        Assert.assertEquals("Summer discount", discount.getDescription());
        Assert.assertTrue(discount.isActive());
        Assert.assertEquals(now, discount.getCreatedAt());
        Assert.assertEquals(now, discount.getUpdatedAt());
    }
}
