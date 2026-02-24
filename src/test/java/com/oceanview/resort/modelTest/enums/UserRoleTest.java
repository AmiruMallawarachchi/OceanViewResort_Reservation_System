package com.oceanview.resort.modelTest.enums;

import com.oceanview.resort.model.enums.UserRole;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserRoleTest {

    @Test
    public void testValues() {
        UserRole[] values = UserRole.values();
        assertEquals(2, values.length);
        assertEquals(UserRole.ADMIN, UserRole.valueOf("ADMIN"));
        assertEquals(UserRole.RESERVATIONIST, UserRole.valueOf("RESERVATIONIST"));
    }
}
