package com.oceanview.resort.repositoryTest;

import com.oceanview.resort.dao.GuestDAO;
import com.oceanview.resort.model.Guest;
import com.oceanview.resort.model.enums.GuestType;
import com.oceanview.resort.repository.GuestRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Tests GuestRepository contract using GuestDAO implementation.
 */
public class GuestRepositoryTest {

    private final GuestRepository repository = new GuestDAO();
    private static final String ID_PREFIX = "REPO_GUEST_" + System.currentTimeMillis();
    private long createdId;
    private String idNumber;

    @After
    public void tearDown() {
        if (createdId > 0) {
            repository.delete(createdId);
        }
    }

    @Test
    public void testCreateFindByIdFindByIdNumberFindAll() {
        Guest g = new Guest();
        g.setFirstName("Repo");
        g.setLastName("Guest");
        g.setEmail(ID_PREFIX + "@test.com");
        g.setPhone("+1234567890");
        g.setAddress("123 Test Street");
        g.setIdNumber(ID_PREFIX);
        g.setGuestType(GuestType.REGULAR);

        Guest created = repository.create(g);
        Assert.assertNotNull(created);
        Assert.assertTrue(created.getId() > 0);
        createdId = created.getId();
        idNumber = created.getIdNumber();

        Guest byId = repository.findById(createdId);
        Assert.assertNotNull(byId);
        Assert.assertEquals(createdId, byId.getId());

        Guest byIdNum = repository.findByIdNumber(idNumber);
        Assert.assertNotNull(byIdNum);
        Assert.assertEquals(idNumber, byIdNum.getIdNumber());

        List<Guest> all = repository.findAll();
        Assert.assertNotNull(all);
        Assert.assertTrue(all.stream().anyMatch(x -> x.getId() == createdId));
    }

    @Test
    public void testUpdateAndDelete() {
        Guest g = new Guest();
        g.setFirstName("Upd");
        g.setLastName("Guest");
        g.setEmail(ID_PREFIX + "upd@test.com");
        g.setPhone("+1234567891");
        g.setAddress("456 Update Ave");
        g.setIdNumber(ID_PREFIX + "upd");
        g.setGuestType(GuestType.REGULAR);
        Guest created = repository.create(g);
        createdId = created.getId();

        created.setLastName("Updated");
        repository.update(created);

        Guest found = repository.findById(createdId);
        Assert.assertEquals("Updated", found.getLastName());

        boolean deleted = repository.delete(createdId);
        Assert.assertTrue(deleted);
        Assert.assertNull(repository.findById(createdId));
        createdId = 0;
    }

    @Test
    public void testFindByIdNotFound() {
        Assert.assertNull(repository.findById(999999L));
    }

    @Test
    public void testFindByIdNumberNotFound() {
        Assert.assertNull(repository.findByIdNumber("NONEXISTENT_ID_NUM"));
    }
}
