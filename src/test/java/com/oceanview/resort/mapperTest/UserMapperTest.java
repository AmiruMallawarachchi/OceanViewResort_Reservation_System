package com.oceanview.resort.mapperTest;

import com.oceanview.resort.dto.UserDTO;
import com.oceanview.resort.mapper.UserMapper;
import com.oceanview.resort.model.User;
import com.oceanview.resort.model.enums.UserRole;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for UserMapper (User <-> UserDTO).
 */
public class UserMapperTest {

    @Test
    public void testToDTO_MapsAllFields() {
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setFullName("Admin User");
        user.setEmail("admin@example.com");
        user.setRole(UserRole.ADMIN);
        user.setActive(true);

        UserDTO dto = UserMapper.toDTO(user);

        Assert.assertNotNull(dto);
        Assert.assertEquals(1L, dto.getId());
        Assert.assertEquals("admin", dto.getUsername());
        Assert.assertEquals("Admin User", dto.getFullName());
        Assert.assertEquals("admin@example.com", dto.getEmail());
        Assert.assertEquals("ADMIN", dto.getRole());
        Assert.assertTrue(dto.isActive());
    }

    @Test
    public void testToDTO_NullUserReturnsNull() {
        Assert.assertNull(UserMapper.toDTO(null));
    }

    @Test
    public void testToEntity_MapsAllFields() {
        UserDTO dto = new UserDTO();
        dto.setId(5L);
        dto.setUsername("res1");
        dto.setFullName("Reservationist One");
        dto.setEmail("res1@example.com");
        dto.setRole("RESERVATIONIST");
        dto.setActive(true);

        User user = UserMapper.toEntity(dto);

        Assert.assertNotNull(user);
        Assert.assertEquals(5L, user.getId());
        Assert.assertEquals("res1", user.getUsername());
        Assert.assertEquals("Reservationist One", user.getFullName());
        Assert.assertEquals("res1@example.com", user.getEmail());
        Assert.assertEquals(UserRole.RESERVATIONIST, user.getRole());
        Assert.assertTrue(user.isActive());
    }

    @Test
    public void testToEntity_NullDtoReturnsNull() {
        Assert.assertNull(UserMapper.toEntity(null));
    }

    @Test
    public void testToEntity_NullRoleLeavesRoleNull() {
        UserDTO dto = new UserDTO();
        dto.setId(2L);
        dto.setUsername("norole");
        dto.setFullName("No Role");
        dto.setEmail("norole@example.com");
        dto.setRole(null);
        dto.setActive(false);

        User user = UserMapper.toEntity(dto);

        Assert.assertNotNull(user);
        Assert.assertNull(user.getRole());
        Assert.assertFalse(user.isActive());
    }
}

