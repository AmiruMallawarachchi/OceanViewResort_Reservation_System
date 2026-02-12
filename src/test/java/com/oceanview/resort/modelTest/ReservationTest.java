package com.oceanview.resort.modelTest;

import com.oceanview.resort.model.Guest;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.model.User;
import com.oceanview.resort.model.enums.ReservationStatus;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Tests for Reservation model (getters/setters, calculateNights, validateDates).
 */
public class ReservationTest {

    @Test
    public void testGettersAndSetters() {
        Reservation reservation = new Reservation();
        Guest guest = new Guest();
        Room room = new Room();
        User user = new User();
        LocalDate checkIn = LocalDate.of(2026, 2, 12);
        LocalDate checkOut = LocalDate.of(2026, 2, 14);
        LocalDateTime createdAt = LocalDateTime.now();

        reservation.setId(1L);
        reservation.setReservationNo("RES-1-101-20260212");
        reservation.setGuest(guest);
        reservation.setRoom(room);
        reservation.setCheckInDate(checkIn);
        reservation.setCheckOutDate(checkOut);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setCreatedBy(user);
        reservation.setCreatedAt(createdAt);

        Assert.assertEquals(1L, reservation.getId());
        Assert.assertEquals("RES-1-101-20260212", reservation.getReservationNo());
        Assert.assertEquals(guest, reservation.getGuest());
        Assert.assertEquals(room, reservation.getRoom());
        Assert.assertEquals(checkIn, reservation.getCheckInDate());
        Assert.assertEquals(checkOut, reservation.getCheckOutDate());
        Assert.assertEquals(ReservationStatus.PENDING, reservation.getStatus());
        Assert.assertEquals(user, reservation.getCreatedBy());
        Assert.assertEquals(createdAt, reservation.getCreatedAt());
    }

    @Test
    public void testCalculateNights() {
        Reservation reservation = new Reservation();
        reservation.setCheckInDate(LocalDate.of(2026, 2, 12));
        reservation.setCheckOutDate(LocalDate.of(2026, 2, 15));

        Assert.assertEquals(3L, reservation.calculateNights());
    }

    @Test
    public void testCalculateNightsWithNullDatesReturnsZero() {
        Reservation reservation = new Reservation();
        reservation.setCheckInDate(null);
        reservation.setCheckOutDate(LocalDate.of(2026, 2, 15));
        Assert.assertEquals(0L, reservation.calculateNights());

        reservation.setCheckInDate(LocalDate.of(2026, 2, 12));
        reservation.setCheckOutDate(null);
        Assert.assertEquals(0L, reservation.calculateNights());
    }

    @Test
    public void testValidateDatesSuccess() {
        Reservation reservation = new Reservation();
        reservation.setCheckInDate(LocalDate.of(2026, 2, 12));
        reservation.setCheckOutDate(LocalDate.of(2026, 2, 13));

        reservation.validateDates(); // should not throw
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateDates_NullDatesThrow() {
        Reservation reservation = new Reservation();
        reservation.setCheckInDate(null);
        reservation.setCheckOutDate(LocalDate.of(2026, 2, 13));
        reservation.validateDates();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateDates_CheckOutBeforeCheckInThrows() {
        Reservation reservation = new Reservation();
        reservation.setCheckInDate(LocalDate.of(2026, 2, 15));
        reservation.setCheckOutDate(LocalDate.of(2026, 2, 14));
        reservation.validateDates();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateDates_CheckOutSameAsCheckInThrows() {
        Reservation reservation = new Reservation();
        LocalDate date = LocalDate.of(2026, 2, 15);
        reservation.setCheckInDate(date);
        reservation.setCheckOutDate(date);
        reservation.validateDates();
    }
}

