package com.oceanview.resort.modelTest.enums;

import com.oceanview.resort.model.enums.GuestType;
import org.junit.Test;

import static org.junit.Assert.*;

public class GuestTypeTest {

    @Test
    public void testValues() {
        GuestType[] values = GuestType.values();
        assertEquals(3, values.length);
        assertEquals(GuestType.REGULAR, GuestType.valueOf("REGULAR"));
        assertEquals(GuestType.VIP, GuestType.valueOf("VIP"));
        assertEquals(GuestType.CORPORATE, GuestType.valueOf("CORPORATE"));
    }
}
