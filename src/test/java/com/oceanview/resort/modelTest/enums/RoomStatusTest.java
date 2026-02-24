package com.oceanview.resort.modelTest.enums;

import com.oceanview.resort.model.enums.RoomStatus;
import org.junit.Test;

import static org.junit.Assert.*;

public class RoomStatusTest {

    @Test
    public void testValues() {
        RoomStatus[] values = RoomStatus.values();
        assertEquals(4, values.length);
        assertEquals(RoomStatus.AVAILABLE, RoomStatus.valueOf("AVAILABLE"));
        assertEquals(RoomStatus.OCCUPIED, RoomStatus.valueOf("OCCUPIED"));
        assertEquals(RoomStatus.MAINTENANCE, RoomStatus.valueOf("MAINTENANCE"));
        assertEquals(RoomStatus.RESERVED, RoomStatus.valueOf("RESERVED"));
    }
}
