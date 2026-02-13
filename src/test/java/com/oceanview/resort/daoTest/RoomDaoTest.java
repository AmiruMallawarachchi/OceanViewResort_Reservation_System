package com.oceanview.resort.daoTest;

import com.oceanview.resort.config.DatabaseConnection;
import com.oceanview.resort.dao.RoomDAO;
import com.oceanview.resort.dao.RoomTypeDAO;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.model.RoomType;
import com.oceanview.resort.model.enums.RoomStatus;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Minimal integration tests for RoomDAO (create/find/update/delete).
 * Creates a RoomType dependency and cleans it up after.
 */
public class RoomDaoTest {

    private RoomDAO roomDAO;
    private RoomTypeDAO roomTypeDAO;

    private long createdRoomTypeId;
    private long createdRoomId;
    private String roomNumber;
    private String typeName;

    @Before
    public void setup() {
        roomDAO = new RoomDAO();
        roomTypeDAO = new RoomTypeDAO();
        createdRoomTypeId = 0;
        createdRoomId = 0;
        int suffix = (int) (System.currentTimeMillis() % 1_000_000);
        roomNumber = "R" + suffix;   // short for DB room_number column limit
        typeName = "T" + suffix;      // short for DB type_name column limit

        RoomType rt = new RoomType();
        rt.setTypeName(typeName);
        rt.setDescription("TDD room type");
        rt.setRatePerNight(new BigDecimal("9000.00"));
        rt.setMaxOccupancy(2);
        rt.setAmenities("WiFi");
        rt.setActive(true);
        createdRoomTypeId = roomTypeDAO.create(rt).getId();
    }

    @After
    public void tearDown() {
        if (createdRoomId > 0) {
            roomDAO.delete(createdRoomId);
        } else if (roomNumber != null) {
            deleteRoomByNumber(roomNumber);
        }
        if (createdRoomTypeId > 0) {
            roomTypeDAO.delete(createdRoomTypeId);
        } else if (typeName != null) {
            deleteRoomTypeByName(typeName);
        }
    }

    @Test
    public void testCreateAndFindByIdAndFindByRoomNumber() {
        Room created = roomDAO.create(buildRoom());
        Assert.assertNotNull(created);
        Assert.assertTrue(created.getId() > 0);
        createdRoomId = created.getId();

        Room foundById = roomDAO.findById(createdRoomId);
        Assert.assertNotNull(foundById);
        Assert.assertEquals(createdRoomId, foundById.getId());
        Assert.assertEquals(roomNumber, foundById.getRoomNumber());
        Assert.assertNotNull(foundById.getRoomType());
        Assert.assertEquals(createdRoomTypeId, foundById.getRoomType().getId());

        Room foundByNo = roomDAO.findByRoomNumber(roomNumber);
        Assert.assertNotNull(foundByNo);
        Assert.assertEquals(createdRoomId, foundByNo.getId());
    }

    @Test
    public void testUpdateThenDelete() {
        Room created = roomDAO.create(buildRoom());
        createdRoomId = created.getId();

        created.setStatus(RoomStatus.MAINTENANCE);
        created.setDescription("Updated");
        created.setFullAccess(true);
        roomDAO.update(created);

        Room found = roomDAO.findById(createdRoomId);
        Assert.assertNotNull(found);
        Assert.assertEquals(RoomStatus.MAINTENANCE, found.getStatus());
        Assert.assertEquals("Updated", found.getDescription());
        Assert.assertTrue(found.isFullAccess());

        boolean deleted = roomDAO.delete(createdRoomId);
        Assert.assertTrue(deleted);
        Assert.assertNull(roomDAO.findById(createdRoomId));
        createdRoomId = 0;
    }

    @Test
    public void testFindAllContainsCreatedRoom() {
        Room created = roomDAO.create(buildRoom());
        createdRoomId = created.getId();

        List<Room> all = roomDAO.findAll();
        Assert.assertNotNull(all);
        Assert.assertTrue(all.stream().anyMatch(r -> roomNumber.equals(r.getRoomNumber())));
    }

    @Test
    public void testFindAvailableIncludesAvailableRoomWhenNoReservations() {
        Room created = roomDAO.create(buildRoom());
        createdRoomId = created.getId();

        List<Room> available = roomDAO.findAvailable(LocalDate.now().plusDays(10), LocalDate.now().plusDays(12));
        Assert.assertNotNull(available);
        Assert.assertTrue(available.stream().anyMatch(r -> roomNumber.equals(r.getRoomNumber())));
    }

    private Room buildRoom() {
        Room room = new Room();
        room.setRoomNumber(roomNumber);
        RoomType rt = new RoomType();
        rt.setId(createdRoomTypeId);
        room.setRoomType(rt);
        room.setFloor(1);
        room.setStatus(RoomStatus.AVAILABLE);
        room.setDescription("TDD room");
        room.setFullAccess(false);
        return room;
    }

    private void deleteRoomByNumber(String number) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM rooms WHERE room_number = ?")) {
            stmt.setString(1, number);
            stmt.executeUpdate();
        } catch (SQLException ignored) {
        }
    }

    private void deleteRoomTypeByName(String name) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM room_types WHERE type_name = ?")) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        } catch (SQLException ignored) {
        }
    }
}

