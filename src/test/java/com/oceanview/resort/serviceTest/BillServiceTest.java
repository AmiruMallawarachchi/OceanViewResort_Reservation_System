package com.oceanview.resort.serviceTest;

import com.oceanview.resort.dto.BillDTO;
import com.oceanview.resort.model.Bill;
import com.oceanview.resort.repository.BillRepository;
import com.oceanview.resort.repository.DiscountRepository;
import com.oceanview.resort.repository.ReservationRepository;
import com.oceanview.resort.repository.RoomRepository;
import com.oceanview.resort.repository.RoomTypeRepository;
import com.oceanview.resort.repository.UserRepository;
import com.oceanview.resort.service.impl.BillServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BillServiceTest {

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
    public void testFindById() {
        Bill bill = new Bill();
        bill.setId(1L);
        bill.setBillNo("BILL-001");
        when(billRepository.findById(1L)).thenReturn(bill);

        BillDTO dto = billService.findById(1L);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("BILL-001", dto.getBillNo());
        verify(billRepository).findById(1L);
    }

    @Test
    public void testFindByIdNotFound() {
        when(billRepository.findById(999L)).thenReturn(null);
        BillDTO dto = billService.findById(999L);
        assertNull(dto);
    }

    @Test
    public void testFindByReservationId() {
        Bill bill = new Bill();
        bill.setId(2L);
        when(billRepository.findByReservationId(10L)).thenReturn(bill);

        BillDTO dto = billService.findByReservationId(10L);
        assertNotNull(dto);
        assertEquals(2L, dto.getId());
        verify(billRepository).findByReservationId(10L);
    }

    @Test
    public void testFindAll() {
        Bill b1 = new Bill();
        b1.setId(1L);
        Bill b2 = new Bill();
        b2.setId(2L);
        when(billRepository.findAll()).thenReturn(Arrays.asList(b1, b2));

        List<BillDTO> result = billService.findAll();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        verify(billRepository).findAll();
    }

    @Test
    public void testFindAllEmpty() {
        when(billRepository.findAll()).thenReturn(Collections.emptyList());
        List<BillDTO> result = billService.findAll();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
