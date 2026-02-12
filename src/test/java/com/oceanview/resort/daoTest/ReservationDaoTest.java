package com.oceanview.resort.daoTest;

import com.oceanview.resort.security.PasswordUtil;
import com.oceanview.resort.config.DatabaseConnection;
import com.oceanview.resort.dao.ReservationDAO;
import com.oceanview.resort.model.Guest;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.model.User;
import com.oceanview.resort.model.enums.ReservationStatus;
import com.oceanview.resort.model.enums.RoomStatus;
import com.oceanview.resort.dao.GuestDAO;
import com.oceanview.resort.dao.RoomDAO;
import com.oceanview.resort.dao.UserDAO;
import com.oceanview.resort.model.enums.GuestType;
import com.oceanview.resort.model.enums.UserRole;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Integration tests for ReservationDAO: create, findById, findByReservationNo,
 * findAll, search, delete, and isRoomAvailable.
 *
 * These tests hit the real database configured in db.properties,
 * similar to {@link UserDaoTest}.
 */
public class ReservationDaoTest {

    private ReservationDAO reservationDAO;
    private GuestDAO guestDAO;
    private RoomDAO roomDAO;
    private UserDAO userDAO;

    private long testGuestId;
    private long testRoomId;
    private long testUserId;
    private String testReservationNo;

    @Before
    public void setup() {
        reservationDAO = new ReservationDAO();
        guestDAO = new GuestDAO();
        roomDAO = new RoomDAO();
        userDAO = new UserDAO();

        createSupportingData();
    }

    @After
    public void tearDown() {
        deleteTestReservationByNumber(testReservationNo);
        deleteSupportingData();
    }

    @Test
    public void testCreateAndFindById() {
        Reservation reservation = buildTestReservation();
        Reservation created = reservationDAO.create(reservation);

        Assert.assertNotNull(created);
        Assert.assertTrue(created.getId() > 0);
        Assert.assertEquals(testReservationNo, created.getReservationNo());

        Reservation found = reservationDAO.findById(created.getId());
        Assert.assertNotNull(found);
        Assert.assertEquals(created.getId(), found.getId());
        Assert.assertEquals(testReservationNo, found.getReservationNo());
        Assert.assertEquals(testGuestId, found.getGuest().getId());
        Assert.assertEquals(testRoomId, found.getRoom().getId());
        Assert.assertEquals(ReservationStatus.PENDING, found.getStatus());
    }

    @Test
    public void testFindByReservationNo() {
        Reservation reservation = reservationDAO.create(buildTestReservation());

        Reservation found = reservationDAO.findByReservationNo(testReservationNo);
        Assert.assertNotNull(found);
        Assert.assertEquals(reservation.getId(), found.getId());
        Assert.assertEquals(testReservationNo, found.getReservationNo());
    }

    @Test
    public void testFindAllIncludesCreatedReservation() {
        reservationDAO.create(buildTestReservation());

        List<Reservation> all = reservationDAO.findAll();
        Assert.assertNotNull(all);
        boolean exists = all.stream().anyMatch(r -> testReservationNo.equals(r.getReservationNo()));
        Assert.assertTrue("Created reservation should be in list", exists);
    }

    @Test
    public void testSearchByReservationNo() {
        reservationDAO.create(buildTestReservation());

        List<Reservation> result = reservationDAO.search(testReservationNo);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertTrue(result.stream().anyMatch(r -> testReservationNo.equals(r.getReservationNo())));
    }

    @Test
    public void testDeleteReservation() {
        Reservation reservation = reservationDAO.create(buildTestReservation());
        long id = reservation.getId();

        boolean deleted = reservationDAO.delete(id);

        Assert.assertTrue(deleted);
        Assert.assertNull(reservationDAO.findById(id));
    }

    @Test
    public void testIsRoomAvailableFalseWhenOverlappingReservationExists() {
        Reservation reservation = reservationDAO.create(buildTestReservation());
        LocalDate checkIn = reservation.getCheckInDate();
        LocalDate checkOut = reservation.getCheckOutDate();

        boolean available = reservationDAO.isRoomAvailable(testRoomId, checkIn, checkOut);

        Assert.assertFalse("Room should not be available when overlapping reservation exists", available);
    }

    @Test
    public void testIsRoomAvailableTrueWhenNoOverlap() {
        reservationDAO.create(buildTestReservation());

        LocalDate checkIn = LocalDate.now().plusDays(30);
        LocalDate checkOut = LocalDate.now().plusDays(32);

        boolean available = reservationDAO.isRoomAvailable(testRoomId, checkIn, checkOut);

        Assert.assertTrue("Room should be available when no overlapping reservation exists", available);
    }

    // ---------- helper methods ----------

    private void createSupportingData() {
        // Create guest
        Guest guest = new Guest();
        guest.setFirstName("ResTest");
        guest.setLastName("Guest");
        guest.setEmail("restest_guest@test.com");
        guest.setPhone("0710000000");
        guest.setAddress("Test Address");
        guest.setIdType("NIC");
        guest.setIdNumber("RESTEST_" + System.currentTimeMillis());
        guest.setNationality("Sri Lankan");
        guest.setGuestType(GuestType.REGULAR);
        testGuestId = guestDAO.create(guest).getId();

        // Create room type + room via SQL to keep dependencies minimal
        try (Connection conn = DatabaseConnection.getConnection()) {
            // room_type (type_name may have length limit - keep short)
            int typeSuffix = (int) (System.currentTimeMillis() % 1_000_000);
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO room_types (type_name, description, rate_per_night, max_occupancy, amenities, is_active) " +
                            "VALUES (?, ?, ?, ?, ?, 1)",
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, "RTT" + typeSuffix);
                stmt.setString(2, "Test type");
                stmt.setBigDecimal(3, new java.math.BigDecimal("10000.00"));
                stmt.setInt(4, 2);
                stmt.setString(5, "AC");
                stmt.executeUpdate();
                try (java.sql.ResultSet keys = stmt.getGeneratedKeys()) {
                    long roomTypeId = 0;
                    if (keys.next()) {
                        roomTypeId = keys.getLong(1);
                    }
                    // room (room_number column is typically VARCHAR(10) - keep short)
                    int roomSuffix = (int) (System.currentTimeMillis() % 1_000_000);
                    try (PreparedStatement roomStmt = conn.prepareStatement(
                            "INSERT INTO rooms (room_number, room_type_id, floor, status, description, is_full_access) " +
                                    "VALUES (?, ?, ?, ?, ?, 0)",
                            PreparedStatement.RETURN_GENERATED_KEYS)) {
                        roomStmt.setString(1, "R" + roomSuffix);
                        roomStmt.setLong(2, roomTypeId);
                        roomStmt.setInt(3, 1);
                        roomStmt.setString(4, RoomStatus.AVAILABLE.name());
                        roomStmt.setString(5, "Reservation test room");
                        roomStmt.executeUpdate();
                        try (java.sql.ResultSet roomKeys = roomStmt.getGeneratedKeys()) {
                            if (roomKeys.next()) {
                                testRoomId = roomKeys.getLong(1);
                            }
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to create supporting data for ReservationDaoTest", ex);
        }

        // Create user
        User user = new User();
        user.setUsername("resdao_test_" + System.currentTimeMillis());
        user.setPasswordHash(PasswordUtil.hashPassword("Pass123!"));
        user.setFullName("Reservation DAO Test User");
        user.setEmail("resdao_user@test.com");
        user.setRole(UserRole.RESERVATIONIST);
        user.setActive(true);
        testUserId = userDAO.create(user).getId();

        testReservationNo = "RES-DAO-" + System.currentTimeMillis();
    }

    private void deleteSupportingData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // delete reservations referencing our guest/room/user
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM reservations WHERE guest_id = ? OR room_id = ? OR created_by = ?")) {
                stmt.setLong(1, testGuestId);
                stmt.setLong(2, testRoomId);
                stmt.setLong(3, testUserId);
                stmt.executeUpdate();
            }
            // delete guest
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM guests WHERE id = ?")) {
                stmt.setLong(1, testGuestId);
                stmt.executeUpdate();
            }
            // delete room + room_type (cascade manually)
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM rooms WHERE id = ?")) {
                stmt.setLong(1, testRoomId);
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM room_types WHERE type_name LIKE 'RTT%'")) {
                stmt.executeUpdate();
            }
            // delete user
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
                stmt.setLong(1, testUserId);
                stmt.executeUpdate();
            }
        } catch (SQLException ignored) {
        }
    }

    private Reservation buildTestReservation() {
        Reservation reservation = new Reservation();
        Guest guest = new Guest();
        guest.setId(testGuestId);
        reservation.setGuest(guest);

        Room room = new Room();
        room.setId(testRoomId);
        reservation.setRoom(room);

        reservation.setReservationNo(testReservationNo);
        reservation.setCheckInDate(LocalDate.now().plusDays(1));
        reservation.setCheckOutDate(LocalDate.now().plusDays(3));
        reservation.setStatus(ReservationStatus.PENDING);

        User createdBy = new User();
        createdBy.setId(testUserId);
        reservation.setCreatedBy(createdBy);
        return reservation;
    }

    private void deleteTestReservationByNumber(String reservationNo) {
        if (reservationNo == null) {
            return;
        }
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM reservations WHERE reservation_no = ?")) {
            stmt.setString(1, reservationNo);
            stmt.executeUpdate();
        } catch (SQLException ignored) {
        }
    }
}

