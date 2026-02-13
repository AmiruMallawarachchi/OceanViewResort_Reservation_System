package com.oceanview.resort.daoTest;

import com.oceanview.resort.config.DatabaseConnection;
import com.oceanview.resort.dao.RoomTypeDAO;
import com.oceanview.resort.model.RoomType;
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
 * Minimal integration tests for RoomTypeDAO (create/find/update/delete).
 * Hits the real database configured in src/main/resources/db.properties.
 */
public class RoomTypeDaoTest {

    private RoomTypeDAO roomTypeDAO;
    private long createdRoomTypeId;
    private String typeName;

    @Before
    public void setup() {
        roomTypeDAO = new RoomTypeDAO();
        createdRoomTypeId = 0;
        typeName = "TYPE_TDD_" + System.currentTimeMillis();
    }

    @After
    public void tearDown() {
        if (createdRoomTypeId > 0) {
            roomTypeDAO.delete(createdRoomTypeId);
        } else if (typeName != null) {
            deleteByTypeName(typeName);
        }
    }

    @Test
    public void testCreateAndFindById() {
        RoomType created = roomTypeDAO.create(buildRoomType());
        Assert.assertNotNull(created);
        Assert.assertTrue(created.getId() > 0);
        createdRoomTypeId = created.getId();

        RoomType found = roomTypeDAO.findById(createdRoomTypeId);
        Assert.assertNotNull(found);
        Assert.assertEquals(createdRoomTypeId, found.getId());
        Assert.assertEquals(typeName, found.getTypeName());
        Assert.assertTrue(found.isActive());
    }

    @Test
    public void testUpdateThenDelete() {
        RoomType created = roomTypeDAO.create(buildRoomType());
        createdRoomTypeId = created.getId();

        created.setDescription("Updated desc");
        created.setRatePerNight(new BigDecimal("12345.00"));
        created.setActive(false);
        roomTypeDAO.update(created);

        RoomType found = roomTypeDAO.findById(createdRoomTypeId);
        Assert.assertNotNull(found);
        Assert.assertEquals("Updated desc", found.getDescription());
        Assert.assertEquals(0, new BigDecimal("12345.00").compareTo(found.getRatePerNight()));
        Assert.assertFalse(found.isActive());

        boolean deleted = roomTypeDAO.delete(createdRoomTypeId);
        Assert.assertTrue(deleted);
        Assert.assertNull(roomTypeDAO.findById(createdRoomTypeId));
        createdRoomTypeId = 0;
    }

    @Test
    public void testFindAllContainsCreatedType() {
        RoomType created = roomTypeDAO.create(buildRoomType());
        createdRoomTypeId = created.getId();

        List<RoomType> all = roomTypeDAO.findAll();
        Assert.assertNotNull(all);
        Assert.assertTrue(all.stream().anyMatch(t -> typeName.equals(t.getTypeName())));
    }

    private RoomType buildRoomType() {
        RoomType rt = new RoomType();
        rt.setTypeName(typeName);
        rt.setDescription("TDD type");
        rt.setRatePerNight(new BigDecimal("10000.00"));
        rt.setMaxOccupancy(2);
        rt.setAmenities("AC");
        rt.setActive(true);
        return rt;
    }

    private void deleteByTypeName(String name) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM room_types WHERE type_name = ?")) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        } catch (SQLException ignored) {
        }
    }
}

