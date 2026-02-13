package com.oceanview.resort.repositoryTest;

import com.oceanview.resort.dao.RoomTypeDAO;
import com.oceanview.resort.model.RoomType;
import com.oceanview.resort.repository.RoomTypeRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

/**
 * Tests RoomTypeRepository contract using RoomTypeDAO implementation.
 */
public class RoomTypeRepositoryTest {

    private final RoomTypeRepository repository = new RoomTypeDAO();
    private long createdId;
    private String typeName = "REPO_RT_" + System.currentTimeMillis();

    @After
    public void tearDown() {
        if (createdId > 0) {
            repository.delete(createdId);
        }
    }

    @Test
    public void testCreateFindByIdFindAll() {
        RoomType rt = new RoomType();
        rt.setTypeName(typeName);
        rt.setDescription("Repo test type");
        rt.setRatePerNight(new BigDecimal("10000"));
        rt.setMaxOccupancy(2);
        rt.setActive(true);

        RoomType created = repository.create(rt);
        Assert.assertNotNull(created);
        Assert.assertTrue(created.getId() > 0);
        createdId = created.getId();

        RoomType found = repository.findById(createdId);
        Assert.assertNotNull(found);
        Assert.assertEquals(typeName, found.getTypeName());

        List<RoomType> all = repository.findAll();
        Assert.assertNotNull(all);
        Assert.assertTrue(all.stream().anyMatch(r -> r.getId() == createdId));
    }

    @Test
    public void testUpdateAndDelete() {
        RoomType rt = new RoomType();
        rt.setTypeName(typeName + "_upd");
        rt.setRatePerNight(new BigDecimal("10000"));
        rt.setMaxOccupancy(2);
        rt.setActive(true);
        RoomType created = repository.create(rt);
        createdId = created.getId();

        created.setMaxOccupancy(4);
        repository.update(created);

        RoomType found = repository.findById(createdId);
        Assert.assertEquals(4, found.getMaxOccupancy());

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
