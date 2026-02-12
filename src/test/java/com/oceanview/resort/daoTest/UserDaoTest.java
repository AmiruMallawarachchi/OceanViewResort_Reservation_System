package com.oceanview.resort.daoTest;

import com.oceanview.resort.dao.UserDAO;
import com.oceanview.resort.model.User;
import com.oceanview.resort.model.enums.UserRole;
import com.oceanview.resort.config.DatabaseConnection;
import com.oceanview.resort.security.PasswordUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Integration tests for UserDAO (create, findById, findByUsername, findByEmail, findAll, update, delete).
 */
public class UserDaoTest {
    private UserDAO userDAO;
    private static final String TEST_USERNAME = "usertest_" + System.currentTimeMillis();
    private static final String TEST_EMAIL = "usertest_" + System.currentTimeMillis() + "@test.com";

    @Before
    public void setup() {
        userDAO = new UserDAO();
    }

    @After
    public void tearDown() {
        deleteTestUserByUsername(TEST_USERNAME);
    }

    @Test
    public void testCreate() {
        User user = new User();
        user.setUsername(TEST_USERNAME);
        user.setPasswordHash(PasswordUtil.hashPassword("Pass123!"));
        user.setFullName("Test User");
        user.setEmail(TEST_EMAIL);
        user.setRole(UserRole.RESERVATIONIST);
        user.setActive(true);
        User created = userDAO.create(user);

        Assert.assertNotNull(created);
        Assert.assertTrue("ID should be generated", created.getId() > 0);
        Assert.assertEquals(TEST_USERNAME, created.getUsername());
        Assert.assertEquals("Test User", created.getFullName());
        Assert.assertEquals(TEST_EMAIL, created.getEmail());
        Assert.assertEquals(UserRole.RESERVATIONIST, created.getRole());
        Assert.assertTrue(created.isActive());
    }

    @Test
    public void testFindById() {
        User user = createTestUser();
        long id = user.getId();

        User found = userDAO.findById(id);

        Assert.assertNotNull(found);
        Assert.assertEquals(id, found.getId());
        Assert.assertEquals(TEST_USERNAME, found.getUsername());
        Assert.assertEquals(TEST_EMAIL, found.getEmail());
    }

    @Test
    public void testFindByIdNotFound() {
        User found = userDAO.findById(999999L);
        Assert.assertNull(found);
    }

    @Test
    public void testFindByUsername() {
        createTestUser();

        User found = userDAO.findByUsername(TEST_USERNAME);

        Assert.assertNotNull(found);
        Assert.assertEquals(TEST_USERNAME, found.getUsername());
        Assert.assertEquals(TEST_EMAIL, found.getEmail());
    }

    @Test
    public void testFindByUsernameNotFound() {
        User found = userDAO.findByUsername("nonexistent_user_xyz");
        Assert.assertNull(found);
    }

    @Test
    public void testFindByEmail() {
        createTestUser();

        User found = userDAO.findByEmail(TEST_EMAIL);

        Assert.assertNotNull(found);
        Assert.assertEquals(TEST_EMAIL, found.getEmail());
        Assert.assertEquals(TEST_USERNAME, found.getUsername());
    }

    @Test
    public void testFindByEmailNotFound() {
        User found = userDAO.findByEmail("nonexistent@test.com");
        Assert.assertNull(found);
    }

    @Test
    public void testFindAll() {
        createTestUser();

        List<User> all = userDAO.findAll();

        Assert.assertNotNull(all);
        Assert.assertTrue(all.size() >= 1);
        boolean found = all.stream().anyMatch(u -> TEST_USERNAME.equals(u.getUsername()));
        Assert.assertTrue("Created user should be in list", found);
    }

    @Test
    public void testUpdate() {
        User user = createTestUser();
        long id = user.getId();
        user.setFullName("Updated Full Name");
        user.setEmail("updated_" + System.currentTimeMillis() + "@test.com");
        user.setActive(false);

        User updated = userDAO.update(user);

        Assert.assertNotNull(updated);
        Assert.assertEquals("Updated Full Name", updated.getFullName());
        Assert.assertFalse(updated.isActive());

        User found = userDAO.findById(id);
        Assert.assertEquals("Updated Full Name", found.getFullName());
        Assert.assertFalse(found.isActive());
    }

    @Test
    public void testDelete() {
        User user = createTestUser();
        long id = user.getId();

        boolean deleted = userDAO.delete(id);

        Assert.assertTrue(deleted);
        Assert.assertNull(userDAO.findById(id));
        Assert.assertNull(userDAO.findByUsername(TEST_USERNAME));
    }

    @Test
    public void testDeleteNonExistent() {
        boolean deleted = userDAO.delete(999999L);
        Assert.assertFalse(deleted);
    }

    private User createTestUser() {
        User user = new User();
        user.setUsername(TEST_USERNAME);
        user.setPasswordHash(PasswordUtil.hashPassword("Pass123!"));
        user.setFullName("Test User");
        user.setEmail(TEST_EMAIL);
        user.setRole(UserRole.RESERVATIONIST);
        user.setActive(true);
        return userDAO.create(user);
    }

    private void deleteTestUserByUsername(String username) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE username = ?")) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException ignored) {
        }
    }
}
