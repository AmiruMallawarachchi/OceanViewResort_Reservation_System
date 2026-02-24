package com.oceanview.resort.modelTest.enums;

import com.oceanview.resort.model.enums.DiscountType;
import org.junit.Test;

import static org.junit.Assert.*;

public class DiscountTypeTest {

    @Test
    public void testValues() {
        DiscountType[] values = DiscountType.values();
        assertEquals(2, values.length);
        assertEquals(DiscountType.GUEST_TYPE, DiscountType.valueOf("GUEST_TYPE"));
        assertEquals(DiscountType.PROMOTION, DiscountType.valueOf("PROMOTION"));
    }
}
