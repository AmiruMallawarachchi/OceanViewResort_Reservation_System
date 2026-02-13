package com.oceanview.resort.modelTest;

import com.oceanview.resort.model.Room;
import com.oceanview.resort.model.RoomType;
import com.oceanview.resort.model.enums.RoomStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RoomTest {

    private Room room;

    @Before
    public void setUp() {
        room = new Room();
    }

    @Test
    public void testDefaultValues() {
        Assert.assertEquals(0L, room.getId());
        Assert.assertNull(room.getRoomNumber());
        Assert.assertNull(room.getRoomType());
        Assert.assertEquals(0, room.getFloor());
        Assert.assertNull(room.getStatus());
        Assert.assertNull(room.getDescription());
        Assert.assertFalse(room.isFullAccess());
    }

    @Test
    public void testGettersAndSetters() {
        RoomType rt = new RoomType();

        room.setId(1L);
        room.setRoomNumber("101");
        room.setRoomType(rt);
        room.setFloor(2);
        room.setStatus(RoomStatus.AVAILABLE);
        room.setDescription("Ocean view");
        room.setFullAccess(true);

        Assert.assertEquals(1L, room.getId());
        Assert.assertEquals("101", room.getRoomNumber());
        Assert.assertEquals(rt, room.getRoomType());
        Assert.assertEquals(2, room.getFloor());
        Assert.assertEquals(RoomStatus.AVAILABLE, room.getStatus());
        Assert.assertEquals("Ocean view", room.getDescription());
        Assert.assertTrue(room.isFullAccess());
    }

    @Test
    public void testConstructorWithArgs() {
        RoomType rt = new RoomType();
        Room r = new Room(2L, "201", rt, 2, RoomStatus.RESERVED, "Mountain view");
        Assert.assertEquals(2L, r.getId());
        Assert.assertEquals("201", r.getRoomNumber());
        Assert.assertEquals(rt, r.getRoomType());
        Assert.assertEquals(2, r.getFloor());
        Assert.assertEquals(RoomStatus.RESERVED, r.getStatus());
        Assert.assertEquals("Mountain view", r.getDescription());
    }
}
