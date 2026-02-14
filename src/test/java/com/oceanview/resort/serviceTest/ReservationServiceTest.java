package com.oceanview.resort.serviceTest;

import com.oceanview.resort.dto.ReservationDTO;
import com.oceanview.resort.model.Guest;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.model.User;
import com.oceanview.resort.model.enums.ReservationStatus;
import com.oceanview.resort.repository.GuestRepository;
import com.oceanview.resort.repository.ReservationRepository;
import com.oceanview.resort.repository.RoomRepository;
import com.oceanview.resort.observer.ReservationSubject;
import com.oceanview.resort.repository.UserRepository;
import com.oceanview.resort.service.ReservationNotificationService;
import com.oceanview.resort.service.impl.ReservationServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Higher-level tests for {@link com.oceanview.resort.service.ReservationService}
 * using the {@link ReservationServiceImpl} implementation.

 * Focuses on:
 *  - findById / findAll / search mapping behavior
 *  - delete contract
 */
@RunWith(MockitoJUnitRunner.class)
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private GuestRepository guestRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReservationNotificationService notificationService;

    private ReservationServiceImpl reservationService;

    @Before
    public void setup() {
        reservationService = new ReservationServiceImpl(
                reservationRepository,
                guestRepository,
                roomRepository,
                userRepository,
                notificationService,
                new ReservationSubject()
        );
    }

    @Test
    public void testFindById_MapsToDTO() {
        Reservation reservation = buildSampleReservation(1L, "RES-1-101-20260212");
        when(reservationRepository.findById(1L)).thenReturn(reservation);

        ReservationDTO dto = reservationService.findById(1L);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("RES-1-101-20260212", dto.getReservationNo());
        assertEquals(1L, dto.getGuestId());
        assertEquals(2L, dto.getRoomId());
        assertEquals("101", dto.getRoomNumber());
        assertEquals("PENDING", dto.getStatus());
    }

    @Test
    public void testFindAll_MapsListToDTOs() {
        Reservation r1 = buildSampleReservation(1L, "RES-1-101-20260212");
        Reservation r2 = buildSampleReservation(2L, "RES-2-102-20260213");
        when(reservationRepository.findAll()).thenReturn(Arrays.asList(r1, r2));

        List<ReservationDTO> result = reservationService.findAll();

        assertEquals(2, result.size());
        assertEquals("RES-1-101-20260212", result.get(0).getReservationNo());
        assertEquals("RES-2-102-20260213", result.get(1).getReservationNo());
    }

    @Test
    public void testSearchDelegatesToRepository() {
        Reservation r = buildSampleReservation(1L, "RES-1-101-20260212");
        when(reservationRepository.search("101")).thenReturn(Collections.singletonList(r));

        List<ReservationDTO> result = reservationService.search("101");

        assertEquals(1, result.size());
        assertEquals("RES-1-101-20260212", result.get(0).getReservationNo());
    }

    @Test
    public void testDeleteDelegatesToRepository() {
        when(reservationRepository.delete(anyLong())).thenReturn(true);

        boolean deleted = reservationService.delete(5L);

        assertTrue(deleted);
        verify(reservationRepository).delete(5L);
    }

    private Reservation buildSampleReservation(long id, String reservationNo) {
        Reservation reservation = new Reservation();
        reservation.setId(id);
        reservation.setReservationNo(reservationNo);

        Guest guest = new Guest();
        guest.setId(1L);
        guest.setFirstName("Amaya");
        guest.setLastName("Perera");
        reservation.setGuest(guest);

        Room room = new Room();
        room.setId(2L);
        room.setRoomNumber("101");
        reservation.setRoom(room);

        reservation.setCheckInDate(LocalDate.of(2026, 2, 12));
        reservation.setCheckOutDate(LocalDate.of(2026, 2, 13));
        reservation.setStatus(ReservationStatus.PENDING);

        User createdBy = new User();
        createdBy.setId(5L);
        createdBy.setUsername("reservationist");
        reservation.setCreatedBy(createdBy);

        return reservation;
    }
}

