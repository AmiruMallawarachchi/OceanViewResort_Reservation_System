package com.oceanview.resort.daoTest;

import com.oceanview.resort.config.DatabaseConnection;
import com.oceanview.resort.dao.DiscountDAO;
import com.oceanview.resort.model.Discount;
import com.oceanview.resort.model.enums.DiscountType;
import com.oceanview.resort.model.enums.GuestType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Minimal integration tests for DiscountDAO (create/find/update/delete/findActive).
 * Hits the real database configured in src/main/resources/db.properties.
 */
public class DiscountDaoTest {

    private DiscountDAO discountDAO;
    private long createdDiscountId;
    private String discountName;

    @Before
    public void setup() {
        discountDAO = new DiscountDAO();
        createdDiscountId = 0;
        discountName = "DISC_TDD_" + System.currentTimeMillis();
    }

    @After
    public void tearDown() {
        if (createdDiscountId > 0) {
            discountDAO.delete(createdDiscountId);
        } else if (discountName != null) {
            deleteByName(discountName);
        }
    }

    @Test
    public void testCreateAndFindById() {
        Discount created = discountDAO.create(buildDiscount(true));
        Assert.assertNotNull(created);
        Assert.assertTrue(created.getId() > 0);
        createdDiscountId = created.getId();

        Discount found = discountDAO.findById(createdDiscountId);
        Assert.assertNotNull(found);
        Assert.assertEquals(createdDiscountId, found.getId());
        Assert.assertEquals(discountName, found.getName());
        Assert.assertEquals(DiscountType.GUEST_TYPE, found.getDiscountType());
        Assert.assertEquals(GuestType.VIP, found.getGuestType());
        Assert.assertTrue(found.isActive());
    }

    @Test
    public void testUpdateThenDelete() {
        Discount created = discountDAO.create(buildDiscount(true));
        createdDiscountId = created.getId();

        created.setPercent(new BigDecimal("12.50"));
        created.setActive(false);
        discountDAO.update(created);

        Discount found = discountDAO.findById(createdDiscountId);
        Assert.assertNotNull(found);
        Assert.assertEquals(0, new BigDecimal("12.50").compareTo(found.getPercent()));
        Assert.assertFalse(found.isActive());

        boolean deleted = discountDAO.delete(createdDiscountId);
        Assert.assertTrue(deleted);
        Assert.assertNull(discountDAO.findById(createdDiscountId));
        createdDiscountId = 0;
    }

    @Test
    public void testFindActiveIncludesCreatedActiveDiscount() {
        Discount created = discountDAO.create(buildDiscount(true));
        createdDiscountId = created.getId();

        List<Discount> active = discountDAO.findActive();
        Assert.assertNotNull(active);
        Assert.assertTrue(active.stream().anyMatch(d -> createdDiscountId == d.getId()));
    }

    @Test
    public void testFindAllContainsCreatedDiscount() {
        Discount created = discountDAO.create(buildDiscount(true));
        createdDiscountId = created.getId();

        List<Discount> all = discountDAO.findAll();
        Assert.assertNotNull(all);
        Assert.assertTrue(all.stream().anyMatch(d -> createdDiscountId == d.getId()));
    }

    private Discount buildDiscount(boolean active) {
        Discount d = new Discount();
        d.setName(discountName);
        d.setDiscountType(DiscountType.GUEST_TYPE);
        d.setGuestType(GuestType.VIP);
        d.setPercent(new BigDecimal("10.00"));
        d.setDescription("TDD discount");
        d.setActive(active);
        return d;
    }

    private void deleteByName(String name) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM discounts WHERE name = ?")) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        } catch (SQLException ignored) {
        }
    }
}

