package com.oceanview.resort.repositoryTest;

import com.oceanview.resort.dao.DiscountDAO;
import com.oceanview.resort.model.Discount;
import com.oceanview.resort.model.enums.DiscountType;
import com.oceanview.resort.model.enums.GuestType;
import com.oceanview.resort.repository.DiscountRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

/**
 * Tests DiscountRepository contract using DiscountDAO implementation.
 */
public class DiscountRepositoryTest {

    private final DiscountRepository repository = new DiscountDAO();
    private long createdId;
    private final String namePrefix = "REPO_DISC_" + System.currentTimeMillis();

    @After
    public void tearDown() {
        if (createdId > 0) {
            repository.delete(createdId);
        }
    }

    @Test
    public void testCreateFindByIdFindAllFindActive() {
        Discount d = new Discount();
        d.setName(namePrefix);
        d.setDiscountType(DiscountType.PROMOTION);
        d.setGuestType(GuestType.REGULAR);
        d.setPercent(new BigDecimal("10"));
        d.setActive(true);

        Discount created = repository.create(d);
        Assert.assertNotNull(created);
        Assert.assertTrue(created.getId() > 0);
        createdId = created.getId();

        Discount found = repository.findById(createdId);
        Assert.assertNotNull(found);
        Assert.assertEquals(createdId, found.getId());
        Assert.assertEquals(namePrefix, found.getName());

        List<Discount> all = repository.findAll();
        Assert.assertNotNull(all);
        Assert.assertTrue(all.stream().anyMatch(x -> x.getId() == createdId));

        List<Discount> active = repository.findActive();
        Assert.assertNotNull(active);
        Assert.assertTrue(active.stream().anyMatch(x -> x.getId() == createdId));
    }

    @Test
    public void testUpdateAndDelete() {
        Discount d = new Discount();
        d.setName(namePrefix + "_upd");
        d.setDiscountType(DiscountType.GUEST_TYPE);
        d.setPercent(new BigDecimal("5"));
        d.setActive(true);
        Discount created = repository.create(d);
        createdId = created.getId();

        created.setPercent(new BigDecimal("15"));
        repository.update(created);

        Discount found = repository.findById(createdId);
        Assert.assertEquals(0, new BigDecimal("15").compareTo(found.getPercent()));

        boolean deleted = repository.delete(createdId);
        Assert.assertTrue(deleted);
        Assert.assertNull(repository.findById(createdId));
        createdId = 0;
    }

    @Test
    public void testFindByIdNotFound() {
        Assert.assertNull(repository.findById(999999L));
    }
}
