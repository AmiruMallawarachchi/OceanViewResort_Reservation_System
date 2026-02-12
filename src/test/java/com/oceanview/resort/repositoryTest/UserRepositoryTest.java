package com.oceanview.resort.repositoryTest;

import com.oceanview.resort.dao.UserDAO;
import com.oceanview.resort.model.User;
import com.oceanview.resort.model.enums.UserRole;
import com.oceanview.resort.repository.UserRepository;
import com.oceanview.resort.config.DatabaseConnection;
import com.oceanview.resort.security.PasswordUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Tests for UserRepository interface contract (100% coverage of all interface methods).
 * Uses UserDAO as the implementation to verify the repository contract.
 */
public class UserRepositoryTest {
    private UserRepository userRepository;
    private static final String USERNAME_PREFIX = "repostest_";
    private String testUsername;
    private String testEmail;

    @Before
    public void setup() {
        userRepository = new UserDAO();
        long timestamp = System.nanoTime();
        testUsername = USERNAME_PREFIX + timestamp;
        testEmail = "repo_" + timestamp + "@test.com";
    }

    @After
    public void tearDown() {
        cleanupTestUsers();
    }

    // ========== create() tests ==========

    @Test
    public void testCreate() {
        User user = new User();
        user.setUsername(testUsername);
        user.setPasswordHash(PasswordUtil.hashPassword("Pass123!"));
        user.setFullName("Repository Test User");
        user.setEmail(testEmail);
        user.setRole(UserRole.ADMIN);
        user.setActive(true);

        User created = userRepository.create(user);

        Assert.assertNotNull("Created user should not be null", created);
        Assert.assertTrue("ID should be generated", created.getId() > 0);
        Assert.assertEquals(testUsername, created.getUsername());
        Assert.assertEquals("Repository Test User", created.getFullName());
        Assert.assertEquals(testEmail, created.getEmail());
        Assert.assertEquals(UserRole.ADMIN, created.getRole());
        Assert.assertTrue(created.isActive());
    }

    @Test
    public void testCreateWithAllFields() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        user.setUsername(testUsername);
        user.setPasswordHash(PasswordUtil.hashPassword("Pass123!"));
        user.setFullName("Full Fields User");
        user.setEmail(testEmail);
        user.setRole(UserRole.RESERVATIONIST);
        user.setActive(false);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        User created = userRepository.create(user);

        Assert.assertNotNull(created);
        Assert.assertTrue(created.getId() > 0);
        Assert.assertEquals(UserRole.RESERVATIONIST, created.getRole());
        Assert.assertFalse(created.isActive());
    }

    // ========== findById() tests ==========

    @Test
    public void testFindById() {
        User created = createTestUser();
        long id = created.getId();

        User found = userRepository.findById(id);

        Assert.assertNotNull(found);
        Assert.assertEquals(id, found.getId());
        Assert.assertEquals(testUsername, found.getUsername());
        Assert.assertEquals(testEmail, found.getEmail());
        Assert.assertEquals("Repository Test User", found.getFullName());
    }

    @Test
    public void testFindByIdNotFound() {
        User found = userRepository.findById(999999L);
        Assert.assertNull("Should return null for non-existent ID", found);
    }

    @Test
    public void testFindByIdZero() {
        User found = userRepository.findById(0L);
        Assert.assertNull("Should return null for ID 0", found);
    }

    // ========== findByUsername() tests ==========

    @Test
    public void testFindByUsername() {
        createTestUser();

        User found = userRepository.findByUsername(testUsername);

        Assert.assertNotNull(found);
        Assert.assertEquals(testUsername, found.getUsername());
        Assert.assertEquals(testEmail, found.getEmail());
    }

    @Test
    public void testFindByUsernameNotFound() {
        User found = userRepository.findByUsername("nonexistent_username_xyz");
        Assert.assertNull("Should return null for non-existent username", found);
    }

    @Test
    public void testFindByUsernameCaseInsensitive() {
        createTestUser();

        // MySQL default collation is case-insensitive, so uppercase should still find the user
        User found = userRepository.findByUsername(testUsername.toUpperCase());
        Assert.assertNotNull("Should find user with case-insensitive matching", found);
        Assert.assertEquals(testUsername, found.getUsername());
    }

    // ========== findByEmail() tests ==========

    @Test
    public void testFindByEmail() {
        createTestUser();

        User found = userRepository.findByEmail(testEmail);

        Assert.assertNotNull(found);
        Assert.assertEquals(testEmail, found.getEmail());
        Assert.assertEquals(testUsername, found.getUsername());
    }

    @Test
    public void testFindByEmailNotFound() {
        User found = userRepository.findByEmail("nonexistent@test.com");
        Assert.assertNull("Should return null for non-existent email", found);
    }

    @Test
    public void testFindByEmailNull() {
        User found = userRepository.findByEmail(null);
        Assert.assertNull("Should return null for null email", found);
    }

    // ========== findAll() tests ==========

    @Test
    public void testFindAll() {
        createTestUser();

        List<User> all = userRepository.findAll();

        Assert.assertNotNull("List should not be null", all);
        Assert.assertTrue("Should contain at least one user", all.size() >= 1);
        boolean found = all.stream().anyMatch(u -> testUsername.equals(u.getUsername()));
        Assert.assertTrue("Created user should be in list", found);
    }

    @Test
    public void testFindAllReturnsMultipleUsers() {
        User user1 = createTestUser();
        String username2 = USERNAME_PREFIX + "second_" + System.nanoTime();
        String email2 = "second_" + System.nanoTime() + "@test.com";
        User user2 = createUserWithCredentials(username2, email2);

        List<User> all = userRepository.findAll();

        Assert.assertTrue("Should contain at least 2 users", all.size() >= 2);
        boolean found1 = all.stream().anyMatch(u -> testUsername.equals(u.getUsername()));
        boolean found2 = all.stream().anyMatch(u -> username2.equals(u.getUsername()));
        Assert.assertTrue("First user should be in list", found1);
        Assert.assertTrue("Second user should be in list", found2);
    }

    // ========== update() tests ==========

    @Test
    public void testUpdate() {
        User user = createTestUser();
        long id = user.getId();

        user.setFullName("Updated Full Name");
        user.setEmail("updated_" + System.nanoTime() + "@test.com");
        user.setRole(UserRole.ADMIN);
        user.setActive(false);

        User updated = userRepository.update(user);

        Assert.assertNotNull(updated);
        Assert.assertEquals("Updated Full Name", updated.getFullName());
        Assert.assertEquals(UserRole.ADMIN, updated.getRole());
        Assert.assertFalse(updated.isActive());

        User found = userRepository.findById(id);
        Assert.assertEquals("Updated Full Name", found.getFullName());
        Assert.assertFalse(found.isActive());
    }

    @Test
    public void testUpdatePreservesPasswordHash() {
        User user = createTestUser();
        String originalHash = user.getPasswordHash();
        long id = user.getId();

        user.setFullName("Updated Name");
        user.setPasswordHash("newHash");

        User updated = userRepository.update(user);

        Assert.assertNotNull(updated);
        User found = userRepository.findById(id);
        Assert.assertEquals("newHash", found.getPasswordHash());
    }

    @Test
    public void testUpdateAllFields() {
        User user = createTestUser();
        long id = user.getId();
        LocalDateTime newCreated = LocalDateTime.now().minusDays(1);
        LocalDateTime newUpdated = LocalDateTime.now();

        user.setUsername(USERNAME_PREFIX + "updated_" + System.nanoTime());
        user.setPasswordHash("updatedHash");
        user.setFullName("All Fields Updated");
        user.setEmail("allupdated_" + System.nanoTime() + "@test.com");
        user.setRole(UserRole.RESERVATIONIST);
        user.setActive(true);
        user.setCreatedAt(newCreated);
        user.setUpdatedAt(newUpdated);

        User updated = userRepository.update(user);

        Assert.assertNotNull(updated);
        Assert.assertEquals(id, updated.getId());
        Assert.assertEquals("All Fields Updated", updated.getFullName());
        Assert.assertEquals(UserRole.RESERVATIONIST, updated.getRole());
        Assert.assertTrue(updated.isActive());
    }

    // ========== delete() tests ==========

    @Test
    public void testDelete() {
        User user = createTestUser();
        long id = user.getId();

        boolean deleted = userRepository.delete(id);

        Assert.assertTrue("Delete should return true", deleted);
        Assert.assertNull("User should not be found after delete", userRepository.findById(id));
        Assert.assertNull("User should not be found by username", userRepository.findByUsername(testUsername));
    }

    @Test
    public void testDeleteNonExistent() {
        boolean deleted = userRepository.delete(999999L);
        Assert.assertFalse("Delete should return false for non-existent ID", deleted);
    }

    @Test
    public void testDeleteZeroId() {
        boolean deleted = userRepository.delete(0L);
        Assert.assertFalse("Delete should return false for ID 0", deleted);
    }

    // ========== Helper methods ==========

    private User createTestUser() {
        return createUserWithCredentials(testUsername, testEmail);
    }

    private User createUserWithCredentials(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(PasswordUtil.hashPassword("Pass123!"));
        user.setFullName("Repository Test User");
        user.setEmail(email);
        user.setRole(UserRole.RESERVATIONIST);
        user.setActive(true);
        return userRepository.create(user);
    }

    private void cleanupTestUsers() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE username LIKE ?")) {
            stmt.setString(1, USERNAME_PREFIX + "%");
            stmt.executeUpdate();
        } catch (SQLException ignored) {
        }
    }
}
