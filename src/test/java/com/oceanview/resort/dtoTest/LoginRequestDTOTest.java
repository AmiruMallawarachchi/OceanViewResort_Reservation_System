package com.oceanview.resort.dtoTest;

import com.oceanview.resort.dto.LoginRequestDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for LoginRequestDTO (getters and setters).
 */
public class LoginRequestDTOTest {
    private LoginRequestDTO loginRequestDTO;

    @Before
    public void setup() {
        loginRequestDTO = new LoginRequestDTO();
    }

    @Test
    public void testGetUsername() {
        loginRequestDTO.setUsername("testuser");
        Assert.assertEquals("testuser", loginRequestDTO.getUsername());
    }

    @Test
    public void testSetUsername() {
        loginRequestDTO.setUsername("admin");
        Assert.assertEquals("admin", loginRequestDTO.getUsername());
    }

    @Test
    public void testGetPassword() {
        loginRequestDTO.setPassword("password123");
        Assert.assertEquals("password123", loginRequestDTO.getPassword());
    }

    @Test
    public void testSetPassword() {
        loginRequestDTO.setPassword("securePass");
        Assert.assertEquals("securePass", loginRequestDTO.getPassword());
    }

    @Test
    public void testUsernameNull() {
        loginRequestDTO.setUsername(null);
        Assert.assertNull(loginRequestDTO.getUsername());
    }

    @Test
    public void testPasswordNull() {
        loginRequestDTO.setPassword(null);
        Assert.assertNull(loginRequestDTO.getPassword());
    }

    @Test
    public void testUsernameEmpty() {
        loginRequestDTO.setUsername("");
        Assert.assertEquals("", loginRequestDTO.getUsername());
    }

    @Test
    public void testPasswordEmpty() {
        loginRequestDTO.setPassword("");
        Assert.assertEquals("", loginRequestDTO.getPassword());
    }

    @Test
    public void testSetBothFields() {
        loginRequestDTO.setUsername("john_doe");
        loginRequestDTO.setPassword("MySecure123!");
        
        Assert.assertEquals("john_doe", loginRequestDTO.getUsername());
        Assert.assertEquals("MySecure123!", loginRequestDTO.getPassword());
    }

    @Test
    public void testUpdateUsername() {
        loginRequestDTO.setUsername("olduser");
        Assert.assertEquals("olduser", loginRequestDTO.getUsername());
        
        loginRequestDTO.setUsername("newuser");
        Assert.assertEquals("newuser", loginRequestDTO.getUsername());
    }

    @Test
    public void testUpdatePassword() {
        loginRequestDTO.setPassword("oldpass");
        Assert.assertEquals("oldpass", loginRequestDTO.getPassword());
        
        loginRequestDTO.setPassword("newpass");
        Assert.assertEquals("newpass", loginRequestDTO.getPassword());
    }
}
