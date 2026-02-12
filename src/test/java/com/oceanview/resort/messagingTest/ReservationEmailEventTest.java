package com.oceanview.resort.messagingTest;

import com.oceanview.resort.messaging.ReservationEmailEvent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for ReservationEmailEvent POJO.
 */
public class ReservationEmailEventTest {

    private ReservationEmailEvent event;

    @Before
    public void setUp() {
        event = new ReservationEmailEvent();
    }

    @Test
    public void testDefaultValues() {
        Assert.assertNull(event.getType());
        Assert.assertEquals(0L, event.getReservationId());
        Assert.assertNull(event.getReservationNo());
        Assert.assertNull(event.getGuestName());
        Assert.assertNull(event.getGuestEmail());
        Assert.assertNull(event.getRoomNumber());
        Assert.assertNull(event.getRoomTypeName());
        Assert.assertNull(event.getCheckInDate());
        Assert.assertNull(event.getCheckOutDate());
        Assert.assertNull(event.getStatus());
    }

    @Test
    public void testGettersAndSetters() {
        event.setType("CONFIRMATION");
        event.setReservationId(123L);
        event.setReservationNo("RES-001");
        event.setGuestName("John Doe");
        event.setGuestEmail("john@example.com");
        event.setRoomNumber("101");
        event.setRoomTypeName("Deluxe");
        event.setCheckInDate("2026-02-15");
        event.setCheckOutDate("2026-02-18");
        event.setStatus("CONFIRMED");

        Assert.assertEquals("CONFIRMATION", event.getType());
        Assert.assertEquals(123L, event.getReservationId());
        Assert.assertEquals("RES-001", event.getReservationNo());
        Assert.assertEquals("John Doe", event.getGuestName());
        Assert.assertEquals("john@example.com", event.getGuestEmail());
        Assert.assertEquals("101", event.getRoomNumber());
        Assert.assertEquals("Deluxe", event.getRoomTypeName());
        Assert.assertEquals("2026-02-15", event.getCheckInDate());
        Assert.assertEquals("2026-02-18", event.getCheckOutDate());
        Assert.assertEquals("CONFIRMED", event.getStatus());
    }
}
