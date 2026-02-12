package com.oceanview.resort.serviceTest.Impl;

import com.oceanview.resort.dto.ReservationDTO;
import com.oceanview.resort.model.Guest;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.model.User;
import com.oceanview.resort.model.enums.ReservationStatus;
import com.oceanview.resort.repository.GuestRepository;
import com.oceanview.resort.repository.ReservationRepository;
import com.oceanview.resort.repository.RoomRepository;
import com.oceanview.resort.repository.UserRepository;
import com.oceanview.resort.service.ReservationNotificationService;
import com.oceanview.resort.service.impl.ReservationServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ReservationServiceImpl}.
 * These tests focus on the core business rules in create/update:
 *  - date validation and room availability
 *  - guest/room/user lookups
 *  - generation of reservation number
 *  - publishing confirmation / cancellation notifications
 */
@RunWith(MockitoJUnitRunner.class)
public class ReservationServiceImplTest {

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
                notificationService
        );
    }

    // ========== create() tests ==========

    @Test
    public void testCreateReservationSuccess_PublishesConfirmation() {
        ReservationDTO dto = new ReservationDTO();
        dto.setGuestId(1L);
        dto.setRoomId(2L);
        dto.setCheckInDate("2026-02-12");
        dto.setCheckOutDate("2026-02-13");

        when(reservationRepository.isRoomAvailable(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(true);
        when(reservationRepository.findByReservationNo(anyString())).thenReturn(null);

        Guest guest = new Guest();
        guest.setId(1L);
        guest.setFirstName("Amaya");
        guest.setLastName("Perera");
        when(guestRepository.findById(1L)).thenReturn(guest);

        Room room = new Room();
        room.setId(2L);
        room.setRoomNumber("101");
        when(roomRepository.findById(2L)).thenReturn(room);

        User user = new User();
        user.setId(5L);
        user.setUsername("reservationist");
        when(userRepository.findById(5L)).thenReturn(user);

        when(reservationRepository.create(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation r = invocation.getArgument(0);
            r.setId(10L);
            r.setStatus(ReservationStatus.PENDING);
            return r;
        });

        ReservationDTO result = reservationService.create(dto, 5L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("PENDING", result.getStatus());

        ArgumentCaptor<Reservation> reservationCaptor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepository).create(reservationCaptor.capture());
        Reservation created = reservationCaptor.getValue();
        assertEquals(guest, created.getGuest());
        assertEquals(room, created.getRoom());

        verify(notificationService).publishConfirmation(created);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateReservation_RoomNotAvailableThrows() {
        ReservationDTO dto = new ReservationDTO();
        dto.setGuestId(1L);
        dto.setRoomId(2L);
        dto.setCheckInDate("2026-02-12");
        dto.setCheckOutDate("2026-02-13");

        when(reservationRepository.isRoomAvailable(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(false);

        reservationService.create(dto, 5L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateReservation_GuestNotFoundThrows() {
        ReservationDTO dto = new ReservationDTO();
        dto.setGuestId(999L);
        dto.setRoomId(2L);
        dto.setCheckInDate("2026-02-12");
        dto.setCheckOutDate("2026-02-13");

        when(reservationRepository.isRoomAvailable(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(true);
        when(guestRepository.findById(999L)).thenReturn(null);

        reservationService.create(dto, 5L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateReservation_RoomNotFoundThrows() {
        ReservationDTO dto = new ReservationDTO();
        dto.setGuestId(1L);
        dto.setRoomId(999L);
        dto.setCheckInDate("2026-02-12");
        dto.setCheckOutDate("2026-02-13");

        when(reservationRepository.isRoomAvailable(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(true);
        when(guestRepository.findById(1L)).thenReturn(new Guest());
        when(roomRepository.findById(999L)).thenReturn(null);

        reservationService.create(dto, 5L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateReservation_UserNotFoundThrows() {
        ReservationDTO dto = new ReservationDTO();
        dto.setGuestId(1L);
        dto.setRoomId(2L);
        dto.setCheckInDate("2026-02-12");
        dto.setCheckOutDate("2026-02-13");

        when(reservationRepository.isRoomAvailable(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(true);
        when(guestRepository.findById(1L)).thenReturn(new Guest());
        when(roomRepository.findById(2L)).thenReturn(new Room());
        when(userRepository.findById(5L)).thenReturn(null);

        reservationService.create(dto, 5L);
    }

    // ========== update() tests ==========

    @Test
    public void testUpdateReservation_StatusChangesToCancelled_PublishesCancellation() {
        Reservation existing = new Reservation();
        existing.setId(1L);
        existing.setStatus(ReservationStatus.PENDING);

        ReservationDTO dto = new ReservationDTO();
        dto.setId(1L);
        dto.setGuestId(1L);
        dto.setRoomId(2L);
        dto.setCheckInDate("2026-02-12");
        dto.setCheckOutDate("2026-02-13");
        dto.setStatus("CANCELLED");

        when(reservationRepository.findById(1L)).thenReturn(existing);
        when(reservationRepository.update(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation updated = invocation.getArgument(0);
            updated.setStatus(ReservationStatus.CANCELLED);
            return updated;
        });

        ReservationDTO result = reservationService.update(dto);

        assertNotNull(result);
        assertEquals("CANCELLED", result.getStatus());

        verify(notificationService).publishCancellation(existing);
    }

    @Test
    public void testUpdateReservation_AlreadyCancelled_DoesNotPublishCancellation() {
        Reservation existing = new Reservation();
        existing.setId(1L);
        existing.setStatus(ReservationStatus.CANCELLED);

        ReservationDTO dto = new ReservationDTO();
        dto.setId(1L);
        dto.setGuestId(1L);
        dto.setRoomId(2L);
        dto.setCheckInDate("2026-02-12");
        dto.setCheckOutDate("2026-02-13");
        dto.setStatus("CANCELLED");

        when(reservationRepository.findById(1L)).thenReturn(existing);
        when(reservationRepository.update(any(Reservation.class))).thenReturn(existing);

        ReservationDTO result = reservationService.update(dto);

        assertNotNull(result);
        assertEquals("CANCELLED", result.getStatus());

        verify(notificationService, never()).publishCancellation(any(Reservation.class));
    }
}

