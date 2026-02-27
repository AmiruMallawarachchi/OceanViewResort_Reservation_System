package com.oceanview.resort.repositoryTest;

import com.oceanview.resort.dao.RoomDAO;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.model.RoomType;
import com.oceanview.resort.model.enums.RoomStatus;
import com.oceanview.resort.repository.RoomRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

/**
 * Tests RoomRepository contract using RoomDAO implementation.
 */
public class RoomRepositoryTest {

    private final RoomRepository repository = new RoomDAO();
    private long createdRoomId;
    private long createdTypeId;
    private final String roomNumber = "REPO_R" + (System.currentTimeMillis() % 100000);
    private final String typeName = "REPO_T" + (System.currentTimeMillis() % 100000);

    @Before
    public void setUp() {
        com.oceanview.resort.dao.RoomTypeDAO typeDAO = new com.oceanview.resort.dao.RoomTypeDAO();
        RoomType rt = new RoomType();
        rt.setTypeName(typeName);
        rt.setRatePerNight(new java.math.BigDecimal("10000"));
        rt.setMaxOccupancy(2);
        rt.setActive(true);
        RoomType createdType = typeDAO.create(rt);
        createdTypeId = createdType.getId();
    }

    @After
    public void tearDown() {
        if (createdRoomId > 0) {
            repository.delete(createdRoomId);
        }
        if (createdTypeId > 0) {
            new com.oceanview.resort.dao.RoomTypeDAO().delete(createdTypeId);
        }
    }

    @Test
    public void testCreateFindByIdFindByRoomNumberFindAllFindAvailable() {
        Room room = new Room();
        room.setRoomNumber(roomNumber);
        RoomType rt = new RoomType();
        rt.setId(createdTypeId);
        room.setRoomType(rt);
        room.setFloor(1);
        room.setStatus(RoomStatus.AVAILABLE);

        Room created = repository.create(room);
        Assert.assertNotNull(created);
        Assert.assertTrue(created.getId() > 0);
        createdRoomId = created.getId();

        Room byId = repository.findById(createdRoomId);
        Assert.assertNotNull(byId);
        Assert.assertEquals(roomNumber, byId.getRoomNumber());

        Room byNum = repository.findByRoomNumber(roomNumber);
        Assert.assertNotNull(byNum);
        Assert.assertEquals(createdRoomId, byNum.getId());

        List<Room> all = repository.findAll();
        Assert.assertNotNull(all);
        Assert.assertTrue(all.stream().anyMatch(r -> r.getId() == createdRoomId));

        List<Room> available = repository.findAvailable(LocalDate.now(), LocalDate.now().plusDays(1));
        Assert.assertNotNull(available);
    }

    @Test
    public void testFindByIdNotFound() {
        Assert.assertNull(repository.findById(999999L));
    }

    @Test
    public void testFindByRoomNumberNotFound() {
        Assert.assertNull(repository.findByRoomNumber("NONEXISTENT_ROOM_NUM"));
    }
}
