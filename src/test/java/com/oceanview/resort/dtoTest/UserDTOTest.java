package com.oceanview.resort.dtoTest;

import com.oceanview.resort.dto.UserDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserDTOTest {

    private UserDTO dto;

    @Before
    public void setup() {
        dto = new UserDTO();
    }

    @Test
    public void testSetAndGetId() {
        dto.setId(42L);
        Assert.assertEquals(42L, dto.getId());
    }

    @Test
    public void testSetAndGetUsername() {
        dto.setUsername("jdoe");
        Assert.assertEquals("jdoe", dto.getUsername());
    }

    @Test
    public void testSetAndGetFullName() {
        dto.setFullName("John Doe");
        Assert.assertEquals("John Doe", dto.getFullName());
    }

    @Test
    public void testSetAndGetEmail() {
        dto.setEmail("john@example.com");
        Assert.assertEquals("john@example.com", dto.getEmail());
    }

    @Test
    public void testSetAndGetRole() {
        dto.setRole("ADMIN");
        Assert.assertEquals("ADMIN", dto.getRole());
    }

    @Test
    public void testSetAndGetActive() {
        dto.setActive(true);
        Assert.assertTrue(dto.isActive());
        dto.setActive(false);
        Assert.assertFalse(dto.isActive());
    }

    @Test
    public void testDefaultValues() {
        Assert.assertEquals(0L, dto.getId());
        Assert.assertNull(dto.getUsername());
        Assert.assertNull(dto.getFullName());
        Assert.assertNull(dto.getEmail());
        Assert.assertNull(dto.getRole());
        Assert.assertFalse(dto.isActive());
    }

    @Test
    public void testUpdateFields() {
        dto.setId(1L);
        dto.setUsername("olduser");
        dto.setFullName("Old Name");
        dto.setEmail("old@example.com");
        dto.setRole("RESERVATIONIST");
        dto.setActive(true);

        // update with new values to ensure setters overwrite correctly
        dto.setId(2L);
        dto.setUsername("newuser");
        dto.setFullName("New Name");
        dto.setEmail("new@example.com");
        dto.setRole("ADMIN");
        dto.setActive(false);

        Assert.assertEquals(2L, dto.getId());
        Assert.assertEquals("newuser", dto.getUsername());
        Assert.assertEquals("New Name", dto.getFullName());
        Assert.assertEquals("new@example.com", dto.getEmail());
        Assert.assertEquals("ADMIN", dto.getRole());
        Assert.assertFalse(dto.isActive());
    }
}

