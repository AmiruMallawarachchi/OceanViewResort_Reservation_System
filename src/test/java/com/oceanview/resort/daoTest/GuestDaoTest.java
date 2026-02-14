package com.oceanview.resort.daoTest;

import com.oceanview.resort.config.DatabaseConnection;
import com.oceanview.resort.dao.GuestDAO;
import com.oceanview.resort.model.Guest;
import com.oceanview.resort.model.enums.GuestType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Minimal integration tests for GuestDAO (create/find/update/delete).
 * Hits the real database configured in src/main/resources/db.properties.
 */
public class GuestDaoTest {

    private GuestDAO guestDAO;
    private long createdGuestId;
    private String idNumber;

    @Before
    public void setup() {
        guestDAO = new GuestDAO();
        createdGuestId = 0;
        idNumber = "GUEST_TDD_" + System.currentTimeMillis();
    }

    @After
    public void tearDown() {
        if (createdGuestId > 0) {
            // best-effort cleanup
            guestDAO.delete(createdGuestId);
        } else if (idNumber != null) {
            deleteByIdNumber(idNumber);
        }
    }

    @Test
    public void testCreateAndFindByIdAndFindByIdNumber() {
        Guest guest = buildGuest();
        Guest created = guestDAO.create(guest);

        Assert.assertNotNull(created);
        Assert.assertTrue(created.getId() > 0);
        createdGuestId = created.getId();

        Guest foundById = guestDAO.findById(createdGuestId);
        Assert.assertNotNull(foundById);
        Assert.assertEquals(createdGuestId, foundById.getId());
        Assert.assertEquals(idNumber, foundById.getIdNumber());

        Guest foundByIdNo = guestDAO.findByIdNumber(idNumber);
        Assert.assertNotNull(foundByIdNo);
        Assert.assertEquals(createdGuestId, foundByIdNo.getId());
    }

    @Test
    public void testUpdateThenDelete() {
        Guest created = guestDAO.create(buildGuest());
        createdGuestId = created.getId();

        created.setFirstName("Updated");
        created.setLastName("Name");
        created.setGuestType(GuestType.VIP);
        guestDAO.update(created);

        Guest found = guestDAO.findById(createdGuestId);
        Assert.assertNotNull(found);
        Assert.assertEquals("Updated", found.getFirstName());
        Assert.assertEquals("Name", found.getLastName());
        Assert.assertEquals(GuestType.VIP, found.getGuestType());

        boolean deleted = guestDAO.delete(createdGuestId);
        Assert.assertTrue(deleted);
        Assert.assertNull(guestDAO.findById(createdGuestId));
        createdGuestId = 0;
    }

    @Test
    public void testFindAllContainsCreatedGuest() {
        Guest created = guestDAO.create(buildGuest());
        createdGuestId = created.getId();

        List<Guest> all = guestDAO.findAll();
        Assert.assertNotNull(all);
        Assert.assertTrue(all.stream().anyMatch(g -> idNumber.equals(g.getIdNumber())));
    }

    private Guest buildGuest() {
        Guest guest = new Guest();
        guest.setFirstName("TDD");
        guest.setLastName("Guest");
        guest.setEmail("tdd_guest_" + System.currentTimeMillis() + "@test.com");
        guest.setPhone("0700000000");
        guest.setAddress("Test Address");
        guest.setIdType("NIC");
        guest.setIdNumber(idNumber);
        guest.setNationality("Test");
        guest.setGuestType(GuestType.REGULAR);
        return guest;
    }

    private void deleteByIdNumber(String idNo) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM guests WHERE id_number = ?")) {
            stmt.setString(1, idNo);
            stmt.executeUpdate();
        } catch (SQLException ignored) {
        }
    }
}

