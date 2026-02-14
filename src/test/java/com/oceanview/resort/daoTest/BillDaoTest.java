package com.oceanview.resort.daoTest;

import com.oceanview.resort.config.DatabaseConnection;
import com.oceanview.resort.dao.BillDAO;
import com.oceanview.resort.dao.GuestDAO;
import com.oceanview.resort.dao.ReservationDAO;
import com.oceanview.resort.dao.RoomDAO;
import com.oceanview.resort.dao.RoomTypeDAO;
import com.oceanview.resort.dao.UserDAO;
import com.oceanview.resort.model.Bill;
import com.oceanview.resort.model.Guest;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.model.RoomType;
import com.oceanview.resort.model.User;
import com.oceanview.resort.model.enums.GuestType;
import com.oceanview.resort.model.enums.ReservationStatus;
import com.oceanview.resort.model.enums.RoomStatus;
import com.oceanview.resort.model.enums.UserRole;
import com.oceanview.resort.security.PasswordUtil;
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
 * Minimal integration tests for BillDAO (create, findById, findByReservationId, findAll).
 * Creates guest, room type, room, user, and reservation in @Before; cleans up in @After.
 */
public class BillDaoTest {

    private BillDAO billDAO;
    private ReservationDAO reservationDAO;
    private GuestDAO guestDAO;
    private RoomDAO roomDAO;
    private RoomTypeDAO roomTypeDAO;
    private UserDAO userDAO;

    private long testGuestId;
    private long testRoomId;
    private long testRoomTypeId;
    private long testUserId;
    private long testReservationId;
    private long createdBillId;
    private String reservationNo;
    private String roomNumber;
    private String typeName;

    @Before
    public void setup() {
        billDAO = new BillDAO();
        reservationDAO = new ReservationDAO();
        guestDAO = new GuestDAO();
        roomDAO = new RoomDAO();
        roomTypeDAO = new RoomTypeDAO();
        userDAO = new UserDAO();

        long ts = System.currentTimeMillis();
        int suffix = (int) (ts % 1_000_000);
        reservationNo = "BILL-TDD-" + ts;
        roomNumber = "BR" + suffix;           // short for DB room_number column limit
        typeName = "BTT" + suffix;             // short for DB type_name column limit

        createSupportingData();
    }

    @After
    public void tearDown() {
        if (createdBillId > 0) {
            deleteBillById(createdBillId);
        }
        deleteSupportingData();
    }

    @Test
    public void testCreateAndFindByIdAndFindByReservationId() {
        Bill bill = buildBill();
        Bill created = billDAO.create(bill);

        Assert.assertNotNull(created);
        Assert.assertTrue(created.getId() > 0);
        createdBillId = created.getId();

        Bill foundById = billDAO.findById(createdBillId);
        Assert.assertNotNull(foundById);
        Assert.assertEquals(createdBillId, foundById.getId());
        Assert.assertEquals(created.getBillNo(), foundById.getBillNo());
        Assert.assertEquals(testReservationId, foundById.getReservation().getId());

        Bill foundByRes = billDAO.findByReservationId(testReservationId);
        Assert.assertNotNull(foundByRes);
        Assert.assertEquals(createdBillId, foundByRes.getId());
    }

    @Test
    public void testFindAllContainsCreatedBill() {
        Bill created = billDAO.create(buildBill());
        createdBillId = created.getId();

        List<Bill> all = billDAO.findAll();
        Assert.assertNotNull(all);
        Assert.assertTrue(all.stream().anyMatch(b -> createdBillId == b.getId()));
    }

    private void createSupportingData() {
        Guest guest = new Guest();
        guest.setFirstName("BillTest");
        guest.setLastName("Guest");
        guest.setEmail("billtest_" + System.currentTimeMillis() + "@test.com");
        guest.setPhone("0720000000");
        guest.setAddress("Bill Test Address");
        guest.setIdType("NIC");
        guest.setIdNumber("BILLTDD_" + System.currentTimeMillis());
        guest.setNationality("Test");
        guest.setGuestType(GuestType.REGULAR);
        testGuestId = guestDAO.create(guest).getId();

        RoomType rt = new RoomType();
        rt.setTypeName(typeName);
        rt.setDescription("Bill test type");
        rt.setRatePerNight(new BigDecimal("12000.00"));
        rt.setMaxOccupancy(2);
        rt.setAmenities("AC");
        rt.setActive(true);
        testRoomTypeId = roomTypeDAO.create(rt).getId();

        Room room = new Room();
        room.setRoomNumber(roomNumber);
        RoomType rtRef = new RoomType();
        rtRef.setId(testRoomTypeId);
        room.setRoomType(rtRef);
        room.setFloor(1);
        room.setStatus(RoomStatus.AVAILABLE);
        room.setDescription("Bill test room");
        room.setFullAccess(false);
        testRoomId = roomDAO.create(room).getId();

        User user = new User();
        user.setUsername("billdao_" + System.currentTimeMillis());
        user.setPasswordHash(PasswordUtil.hashPassword("Pass123!"));
        user.setFullName("Bill DAO Test User");
        user.setEmail("billdao@test.com");
        user.setRole(UserRole.RESERVATIONIST);
        user.setActive(true);
        testUserId = userDAO.create(user).getId();

        Reservation res = new Reservation();
        Guest gRef = new Guest();
        gRef.setId(testGuestId);
        res.setGuest(gRef);
        Room rRef = new Room();
        rRef.setId(testRoomId);
        res.setRoom(rRef);
        res.setReservationNo(reservationNo);
        res.setCheckInDate(LocalDate.now().plusDays(1));
        res.setCheckOutDate(LocalDate.now().plusDays(3));
        res.setStatus(ReservationStatus.PENDING);
        User uRef = new User();
        uRef.setId(testUserId);
        res.setCreatedBy(uRef);
        testReservationId = reservationDAO.create(res).getId();
    }

    private Bill buildBill() {
        Bill bill = new Bill();
        bill.setBillNo("BILLNO-" + System.currentTimeMillis());
        Reservation res = new Reservation();
        res.setId(testReservationId);
        bill.setReservation(res);
        bill.setNumberOfNights(2);
        bill.setRoomRate(new BigDecimal("12000.00"));
        bill.setTotalAmount(new BigDecimal("24000.00"));
        bill.setDiscountAmount(BigDecimal.ZERO);
        bill.setTaxAmount(new BigDecimal("2400.00"));
        bill.setNetAmount(new BigDecimal("26400.00"));
        User genBy = new User();
        genBy.setId(testUserId);
        bill.setGeneratedBy(genBy);
        return bill;
    }

    private void deleteBillById(long id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM bills WHERE id = ?")) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException ignored) {
        }
    }

    private void deleteSupportingData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (testReservationId > 0) {
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM reservations WHERE id = ?")) {
                    stmt.setLong(1, testReservationId);
                    stmt.executeUpdate();
                }
            }
            if (testGuestId > 0) {
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM guests WHERE id = ?")) {
                    stmt.setLong(1, testGuestId);
                    stmt.executeUpdate();
                }
            }
            if (testRoomId > 0) {
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM rooms WHERE id = ?")) {
                    stmt.setLong(1, testRoomId);
                    stmt.executeUpdate();
                }
            }
            if (testRoomTypeId > 0) {
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM room_types WHERE id = ?")) {
                    stmt.setLong(1, testRoomTypeId);
                    stmt.executeUpdate();
                }
            }
            if (testUserId > 0) {
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
                    stmt.setLong(1, testUserId);
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException ignored) {
        }
    }
}
