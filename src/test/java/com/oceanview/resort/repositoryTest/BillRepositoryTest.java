package com.oceanview.resort.repositoryTest;

import com.oceanview.resort.dao.BillDAO;
import com.oceanview.resort.model.Bill;
import com.oceanview.resort.repository.BillRepository;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Tests BillRepository contract using BillDAO implementation.
 */
public class BillRepositoryTest {

    private final BillRepository repository = new BillDAO();

    @Test
    public void testFindByIdNotFound() {
        Bill found = repository.findById(999999L);
        Assert.assertNull(found);
    }

    @Test
    public void testFindByReservationIdNotFound() {
        Bill found = repository.findByReservationId(999999L);
        Assert.assertNull(found);
    }

    @Test
    public void testFindAllReturnsList() {
        List<Bill> all = repository.findAll();
        Assert.assertNotNull(all);
    }
}
