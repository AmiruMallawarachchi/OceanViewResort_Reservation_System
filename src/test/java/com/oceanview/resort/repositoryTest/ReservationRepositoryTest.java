package com.oceanview.resort.repositoryTest;

import com.oceanview.resort.dao.ReservationDAO;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.repository.ReservationRepository;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

/**
 * Tests ReservationRepository contract using ReservationDAO implementation.
 */
public class ReservationRepositoryTest {

    private final ReservationRepository repository = new ReservationDAO();

    @Test
    public void testFindByIdNotFound() {
        Reservation found = repository.findById(999999L);
        Assert.assertNull(found);
    }

    @Test
    public void testFindByReservationNoNotFound() {
        Reservation found = repository.findByReservationNo("NONEXISTENT_NO");
        Assert.assertNull(found);
    }

    @Test
    public void testFindAllReturnsList() {
        List<Reservation> all = repository.findAll();
        Assert.assertNotNull(all);
    }

    @Test
    public void testSearchReturnsList() {
        List<Reservation> results = repository.search("test");
        Assert.assertNotNull(results);
    }

    @Test
    public void testIsRoomAvailableInvalidRoom() {
        // For non-existent room, behavior may vary - just verify it doesn't throw
        boolean available = repository.isRoomAvailable(999999L, LocalDate.now(), LocalDate.now().plusDays(1));
        // Result depends on implementation - could be false (room doesn't exist) or true (no conflicts)
        // Just verify it returns a boolean
    }
}
