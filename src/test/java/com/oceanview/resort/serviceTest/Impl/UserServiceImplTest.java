package com.oceanview.resort.serviceTest.Impl;

import com.oceanview.resort.security.PasswordUtil;
import com.oceanview.resort.dto.UserDTO;
import com.oceanview.resort.model.User;
import com.oceanview.resort.model.enums.UserRole;
import com.oceanview.resort.repository.UserRepository;
import com.oceanview.resort.service.impl.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Comprehensive tests for UserServiceImpl with 100% coverage.
 * Tests all methods, branches, and edge cases.
 */
@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserServiceImpl userService;

    @Before
    public void setup() {
        userService = new UserServiceImpl(userRepository);
    }

    // ========== authenticate() tests ==========

    @Test
    public void testAuthenticateSuccess() {
        String rawPassword = "password123";
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPasswordHash(PasswordUtil.hashPassword(rawPassword));
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        user.setRole(UserRole.ADMIN);
        user.setActive(true);

        when(userRepository.findByUsername("testuser")).thenReturn(user);

        UserDTO result = userService.authenticate("testuser", rawPassword);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("Test User", result.getFullName());
        assertEquals("ADMIN", result.getRole());
        assertTrue(result.isActive());
    }

    @Test
    public void testAuthenticateWrongPassword() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPasswordHash(PasswordUtil.hashPassword("correct"));
        user.setRole(UserRole.ADMIN);
        user.setActive(true);

        when(userRepository.findByUsername("testuser")).thenReturn(user);

        UserDTO result = userService.authenticate("testuser", "wrong");

        assertNull(result);
    }

    @Test
    public void testAuthenticateUserNotFound() {
        when(userRepository.findByUsername("nobody")).thenReturn(null);

        UserDTO result = userService.authenticate("nobody", "any");

        assertNull(result);
    }

    @Test
    public void testAuthenticateInactiveUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("inactive");
        user.setPasswordHash(PasswordUtil.hashPassword("pass"));
        user.setActive(false);

        when(userRepository.findByUsername("inactive")).thenReturn(user);

        UserDTO result = userService.authenticate("inactive", "pass");

        assertNull(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAuthenticateBlankUsername() {
        userService.authenticate("", "password");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAuthenticateNullUsername() {
        userService.authenticate(null, "password");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAuthenticateBlankPassword() {
        userService.authenticate("user", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAuthenticateNullPassword() {
        userService.authenticate("user", null);
    }

    // ========== createUser() tests ==========

    @Test
    public void testCreateUserSuccess() {
        User savedUser = new User();
        savedUser.setId(10L);
        savedUser.setUsername("newuser");
        savedUser.setFullName("New User");
        savedUser.setEmail("new@example.com");
        savedUser.setRole(UserRole.RESERVATIONIST);
        savedUser.setActive(true);

        when(userRepository.create(any(User.class))).thenReturn(savedUser);

        UserDTO dto = new UserDTO();
        dto.setUsername("newuser");
        dto.setFullName("New User");
        dto.setEmail("new@example.com");
        dto.setRole("RESERVATIONIST");

        UserDTO result = userService.createUser(dto, "SecurePass1!");

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("newuser", result.getUsername());
        assertEquals("New User", result.getFullName());
        assertEquals("RESERVATIONIST", result.getRole());
        assertTrue(result.isActive());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).create(captor.capture());
        User created = captor.getValue();
        assertNotNull(created.getPasswordHash());
        assertNotEquals("SecurePass1!", created.getPasswordHash());
        assertTrue(created.isActive());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserBlankUsername() {
        UserDTO dto = new UserDTO();
        dto.setUsername("");
        dto.setRole("ADMIN");
        userService.createUser(dto, "password");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserNullUsername() {
        UserDTO dto = new UserDTO();
        dto.setUsername(null);
        dto.setRole("ADMIN");
        userService.createUser(dto, "password");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserBlankRole() {
        UserDTO dto = new UserDTO();
        dto.setUsername("user");
        dto.setRole("");
        userService.createUser(dto, "password");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserNullRole() {
        UserDTO dto = new UserDTO();
        dto.setUsername("user");
        dto.setRole(null);
        userService.createUser(dto, "password");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserBlankPassword() {
        UserDTO dto = new UserDTO();
        dto.setUsername("user");
        dto.setRole("ADMIN");
        userService.createUser(dto, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserNullPassword() {
        UserDTO dto = new UserDTO();
        dto.setUsername("user");
        dto.setRole("ADMIN");
        userService.createUser(dto, null);
    }

    // ========== updateUser() tests ==========

    @Test
    public void testUpdateUserSuccess() {
        User existing = new User();
        existing.setId(5L);
        existing.setPasswordHash("existingHash");
        User updatedUser = new User();
        updatedUser.setId(5L);
        updatedUser.setUsername("updated");
        updatedUser.setFullName("Updated Name");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setRole(UserRole.ADMIN);
        updatedUser.setActive(true);
        updatedUser.setPasswordHash("existingHash");

        when(userRepository.findById(5L)).thenReturn(existing);
        when(userRepository.update(any(User.class))).thenReturn(updatedUser);

        UserDTO dto = new UserDTO();
        dto.setId(5L);
        dto.setUsername("updated");
        dto.setFullName("Updated Name");
        dto.setEmail("updated@example.com");
        dto.setRole("ADMIN");
        dto.setActive(true);

        UserDTO result = userService.updateUser(dto);

        assertNotNull(result);
        assertEquals(5L, result.getId());
        assertEquals("updated", result.getUsername());
        assertEquals("Updated Name", result.getFullName());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).update(captor.capture());
        User updated = captor.getValue();
        assertEquals("existingHash", updated.getPasswordHash());
    }

    @Test
    public void testUpdateUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(null);

        UserDTO dto = new UserDTO();
        dto.setId(999L);
        dto.setUsername("x");
        dto.setRole("ADMIN");

        UserDTO result = userService.updateUser(dto);

        assertNull(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateUserBlankRole() {
        UserDTO dto = new UserDTO();
        dto.setId(1L);
        dto.setUsername("user");
        dto.setRole("");
        userService.updateUser(dto);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateUserNullRole() {
        UserDTO dto = new UserDTO();
        dto.setId(1L);
        dto.setUsername("user");
        dto.setRole(null);
        userService.updateUser(dto);
    }

    // ========== deleteUser() tests ==========

    @Test
    public void testDeleteUserSuccess() {
        when(userRepository.delete(3L)).thenReturn(true);

        boolean result = userService.deleteUser(3L);

        assertTrue(result);
        verify(userRepository).delete(3L);
    }

    @Test
    public void testDeleteUserNotFound() {
        when(userRepository.delete(999L)).thenReturn(false);

        boolean result = userService.deleteUser(999L);

        assertFalse(result);
    }

    // ========== findById() tests ==========

    @Test
    public void testFindByIdSuccess() {
        User user = new User();
        user.setId(2L);
        user.setUsername("findme");
        user.setFullName("Find Me");
        user.setRole(UserRole.RESERVATIONIST);
        user.setActive(true);

        when(userRepository.findById(2L)).thenReturn(user);

        UserDTO result = userService.findById(2L);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("findme", result.getUsername());
        assertEquals("Find Me", result.getFullName());
        assertEquals("RESERVATIONIST", result.getRole());
    }

    @Test
    public void testFindByIdNotFound() {
        when(userRepository.findById(999L)).thenReturn(null);

        UserDTO result = userService.findById(999L);

        assertNull(result);
    }

    // ========== findByUsername() tests ==========

    @Test
    public void testFindByUsernameSuccess() {
        User user = new User();
        user.setId(3L);
        user.setUsername("jdoe");
        user.setFullName("John Doe");
        user.setRole(UserRole.RESERVATIONIST);

        when(userRepository.findByUsername("jdoe")).thenReturn(user);

        UserDTO result = userService.findByUsername("jdoe");

        assertNotNull(result);
        assertEquals("jdoe", result.getUsername());
        assertEquals("John Doe", result.getFullName());
    }

    @Test
    public void testFindByUsernameNotFound() {
        when(userRepository.findByUsername("nobody")).thenReturn(null);

        UserDTO result = userService.findByUsername("nobody");

        assertNull(result);
    }

    // ========== findByEmail() tests ==========

    @Test
    public void testFindByEmailSuccess() {
        User user = new User();
        user.setId(4L);
        user.setEmail("email@example.com");
        user.setUsername("emailuser");
        user.setRole(UserRole.ADMIN);

        when(userRepository.findByEmail("email@example.com")).thenReturn(user);

        UserDTO result = userService.findByEmail("email@example.com");

        assertNotNull(result);
        assertEquals("email@example.com", result.getEmail());
        assertEquals("emailuser", result.getUsername());
    }

    @Test
    public void testFindByEmailNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(null);

        UserDTO result = userService.findByEmail("nonexistent@example.com");

        assertNull(result);
    }

    // ========== findAll() tests ==========

    @Test
    public void testFindAllWithUsers() {
        User u1 = new User();
        u1.setId(1L);
        u1.setUsername("u1");
        u1.setRole(UserRole.ADMIN);
        User u2 = new User();
        u2.setId(2L);
        u2.setUsername("u2");
        u2.setRole(UserRole.RESERVATIONIST);

        when(userRepository.findAll()).thenReturn(Arrays.asList(u1, u2));

        List<UserDTO> result = userService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("u1", result.get(0).getUsername());
        assertEquals("u2", result.get(1).getUsername());
    }

    @Test
    public void testFindAllEmpty() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        List<UserDTO> result = userService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
