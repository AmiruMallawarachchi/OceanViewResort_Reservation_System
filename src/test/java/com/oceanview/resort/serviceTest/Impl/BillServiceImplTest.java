package com.oceanview.resort.serviceTest.Impl;

import com.oceanview.resort.dto.BillDTO;
import com.oceanview.resort.model.*;
import com.oceanview.resort.repository.*;
import com.oceanview.resort.service.impl.BillServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BillServiceImplTest {

    @Mock
    private BillRepository billRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private RoomTypeRepository roomTypeRepository;
    @Mock
    private DiscountRepository discountRepository;
    @Mock
    private UserRepository userRepository;

    private BillServiceImpl billService;

    @Before
    public void setup() {
        billService = new BillServiceImpl(billRepository, reservationRepository, roomRepository,
                roomTypeRepository, discountRepository, userRepository);
    }

    @Test
    public void testGenerate_reservationNotFound_returnsNull() {
        when(reservationRepository.findById(999L)).thenReturn(null);
        BillDTO result = billService.generate(999L, null, null, null, 1L);
        assertNull(result);
        verify(reservationRepository).findById(999L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGenerate_actualCheckoutBeforeCheckIn_throws() {
        Reservation res = new Reservation();
        res.setId(1L);
        res.setCheckInDate(LocalDate.of(2026, 2, 15));
        res.setCheckOutDate(LocalDate.of(2026, 2, 18));
        Room room = new Room();
        room.setId(10L);
        res.setRoom(room);
        RoomType rt = new RoomType();
        rt.setId(5L);
        room.setRoomType(rt);
        lenient().when(reservationRepository.findById(1L)).thenReturn(res);
        lenient().when(roomRepository.findById(10L)).thenReturn(room);
        lenient().when(roomTypeRepository.findById(5L)).thenReturn(rt);

        billService.generate(1L, null, null, LocalDate.of(2026, 2, 14), 1L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGenerate_actualCheckoutAfterPlannedCheckOut_throws() {
        Reservation res = new Reservation();
        res.setId(1L);
        res.setCheckInDate(LocalDate.of(2026, 2, 15));
        res.setCheckOutDate(LocalDate.of(2026, 2, 18));
        Room room = new Room();
        room.setId(10L);
        res.setRoom(room);
        RoomType rt = new RoomType();
        rt.setId(5L);
        room.setRoomType(rt);
        lenient().when(reservationRepository.findById(1L)).thenReturn(res);
        lenient().when(roomRepository.findById(10L)).thenReturn(room);
        lenient().when(roomTypeRepository.findById(5L)).thenReturn(rt);

        billService.generate(1L, null, null, LocalDate.of(2026, 2, 20), 1L);
    }

    @Test
    public void testGenerate_success_createsBill() {
        Reservation res = new Reservation();
        res.setId(1L);
        res.setCheckInDate(LocalDate.of(2026, 2, 15));
        res.setCheckOutDate(LocalDate.of(2026, 2, 18));
        Room room = new Room();
        room.setId(10L);
        res.setRoom(room);
        res.setGuest(new Guest());
        when(reservationRepository.findById(1L)).thenReturn(res);
        when(roomRepository.findById(10L)).thenReturn(room);
        RoomType rt = new RoomType();
        rt.setId(5L);
        rt.setRatePerNight(new BigDecimal("100"));
        room.setRoomType(rt);
        when(roomTypeRepository.findById(5L)).thenReturn(rt);
        // discountRepository might not be used directly by BillServiceImpl anymore; mark stubbing lenient
        lenient().when(discountRepository.findActive()).thenReturn(Collections.emptyList());
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(user);

        Bill saved = new Bill();
        saved.setId(100L);
        saved.setBillNo("BILL-ABC12345");
        saved.setReservation(res);
        saved.setNumberOfNights(3);
        saved.setRoomRate(new BigDecimal("100"));
        saved.setTotalAmount(new BigDecimal("300"));
        saved.setDiscountAmount(BigDecimal.ZERO);
        saved.setTaxAmount(new BigDecimal("30"));
        saved.setNetAmount(new BigDecimal("330"));
        saved.setGeneratedBy(user);
        saved.setGeneratedAt(LocalDateTime.now());
        when(billRepository.create(any(Bill.class))).thenReturn(saved);

        BillDTO result = billService.generate(1L, null, null, null, 1L);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("BILL-ABC12345", result.getBillNo());
        assertEquals(3, result.getNumberOfNights());
        verify(billRepository).create(any(Bill.class));
    }
}
