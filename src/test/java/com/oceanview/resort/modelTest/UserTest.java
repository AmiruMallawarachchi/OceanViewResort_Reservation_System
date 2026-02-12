package com.oceanview.resort.modelTest;

import com.oceanview.resort.model.User;
import com.oceanview.resort.model.enums.UserRole;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;

/**
 * Tests for User model (getters, setters, and role helpers).
 */
public class UserTest {

    @Test
    public void testGettersAndSetters() {
        User user = new User();
        LocalDateTime now = LocalDateTime.now();

        user.setId(10L);
        user.setUsername("jdoe");
        user.setPasswordHash("hash");
        user.setFullName("John Doe");
        user.setEmail("john@example.com");
        user.setRole(UserRole.ADMIN);
        user.setActive(true);
        user.setCreatedAt(now);
        user.setUpdatedAt(now.plusDays(1));

        Assert.assertEquals(10L, user.getId());
        Assert.assertEquals("jdoe", user.getUsername());
        Assert.assertEquals("hash", user.getPasswordHash());
        Assert.assertEquals("John Doe", user.getFullName());
        Assert.assertEquals("john@example.com", user.getEmail());
        Assert.assertEquals(UserRole.ADMIN, user.getRole());
        Assert.assertTrue(user.isActive());
        Assert.assertEquals(now, user.getCreatedAt());
        Assert.assertEquals(now.plusDays(1), user.getUpdatedAt());
    }

    @Test
    public void testIsAdminAndIsReservationist() {
        User user = new User();

        user.setRole(UserRole.ADMIN);
        Assert.assertTrue(user.isAdmin());
        Assert.assertFalse(user.isReservationist());

        user.setRole(UserRole.RESERVATIONIST);
        Assert.assertFalse(user.isAdmin());
        Assert.assertTrue(user.isReservationist());
    }

    @Test
    public void testRoleNullMakesHelpersFalse() {
        User user = new User();
        user.setRole(null);
        Assert.assertFalse(user.isAdmin());
        Assert.assertFalse(user.isReservationist());
    }

    @Test
    public void testAllArgsConstructorSetsFields() {
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime updated = created.plusHours(2);

        User user = new User(
                5L,
                "constructorUser",
                "hash123",
                "Constructor User",
                "ctor@example.com",
                UserRole.RESERVATIONIST,
                true,
                created,
                updated
        );

        Assert.assertEquals(5L, user.getId());
        Assert.assertEquals("constructorUser", user.getUsername());
        Assert.assertEquals("hash123", user.getPasswordHash());
        Assert.assertEquals("Constructor User", user.getFullName());
        Assert.assertEquals("ctor@example.com", user.getEmail());
        Assert.assertEquals(UserRole.RESERVATIONIST, user.getRole());
        Assert.assertTrue(user.isActive());
        Assert.assertEquals(created, user.getCreatedAt());
        Assert.assertEquals(updated, user.getUpdatedAt());
    }
}

