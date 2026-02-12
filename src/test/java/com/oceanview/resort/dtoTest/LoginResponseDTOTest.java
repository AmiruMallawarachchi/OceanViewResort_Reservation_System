package com.oceanview.resort.dtoTest;

import com.oceanview.resort.dto.LoginResponseDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for LoginResponseDTO (getters and setters).
 */
public class LoginResponseDTOTest {
    private LoginResponseDTO loginResponseDTO;

    @Before
    public void setup() {
        loginResponseDTO = new LoginResponseDTO();
    }

    @Test
    public void testGetUserId() {
        loginResponseDTO.setUserId(1L);
        Assert.assertEquals(1L, loginResponseDTO.getUserId());
    }

    @Test
    public void testSetUserId() {
        loginResponseDTO.setUserId(100L);
        Assert.assertEquals(100L, loginResponseDTO.getUserId());
    }

    @Test
    public void testGetUsername() {
        loginResponseDTO.setUsername("testuser");
        Assert.assertEquals("testuser", loginResponseDTO.getUsername());
    }

    @Test
    public void testSetUsername() {
        loginResponseDTO.setUsername("admin");
        Assert.assertEquals("admin", loginResponseDTO.getUsername());
    }

    @Test
    public void testGetRole() {
        loginResponseDTO.setRole("ADMIN");
        Assert.assertEquals("ADMIN", loginResponseDTO.getRole());
    }

    @Test
    public void testSetRole() {
        loginResponseDTO.setRole("RESERVATIONIST");
        Assert.assertEquals("RESERVATIONIST", loginResponseDTO.getRole());
    }

    @Test
    public void testUserIdZero() {
        loginResponseDTO.setUserId(0L);
        Assert.assertEquals(0L, loginResponseDTO.getUserId());
    }

    @Test
    public void testUserIdNegative() {
        loginResponseDTO.setUserId(-1L);
        Assert.assertEquals(-1L, loginResponseDTO.getUserId());
    }

    @Test
    public void testUsernameNull() {
        loginResponseDTO.setUsername(null);
        Assert.assertNull(loginResponseDTO.getUsername());
    }

    @Test
    public void testRoleNull() {
        loginResponseDTO.setRole(null);
        Assert.assertNull(loginResponseDTO.getRole());
    }

    @Test
    public void testUsernameEmpty() {
        loginResponseDTO.setUsername("");
        Assert.assertEquals("", loginResponseDTO.getUsername());
    }

    @Test
    public void testRoleEmpty() {
        loginResponseDTO.setRole("");
        Assert.assertEquals("", loginResponseDTO.getRole());
    }

    @Test
    public void testSetAllFields() {
        loginResponseDTO.setUserId(5L);
        loginResponseDTO.setUsername("john_doe");
        loginResponseDTO.setRole("ADMIN");
        
        Assert.assertEquals(5L, loginResponseDTO.getUserId());
        Assert.assertEquals("john_doe", loginResponseDTO.getUsername());
        Assert.assertEquals("ADMIN", loginResponseDTO.getRole());
    }

    @Test
    public void testSetAllFieldsReservationist() {
        loginResponseDTO.setUserId(10L);
        loginResponseDTO.setUsername("reservationist1");
        loginResponseDTO.setRole("RESERVATIONIST");
        
        Assert.assertEquals(10L, loginResponseDTO.getUserId());
        Assert.assertEquals("reservationist1", loginResponseDTO.getUsername());
        Assert.assertEquals("RESERVATIONIST", loginResponseDTO.getRole());
    }

    @Test
    public void testUpdateUserId() {
        loginResponseDTO.setUserId(1L);
        Assert.assertEquals(1L, loginResponseDTO.getUserId());
        
        loginResponseDTO.setUserId(999L);
        Assert.assertEquals(999L, loginResponseDTO.getUserId());
    }

    @Test
    public void testUpdateUsername() {
        loginResponseDTO.setUsername("olduser");
        Assert.assertEquals("olduser", loginResponseDTO.getUsername());
        
        loginResponseDTO.setUsername("newuser");
        Assert.assertEquals("newuser", loginResponseDTO.getUsername());
    }

    @Test
    public void testUpdateRole() {
        loginResponseDTO.setRole("ADMIN");
        Assert.assertEquals("ADMIN", loginResponseDTO.getRole());
        
        loginResponseDTO.setRole("RESERVATIONIST");
        Assert.assertEquals("RESERVATIONIST", loginResponseDTO.getRole());
    }
}
