package com.oceanview.resort.mapperTest;

import com.oceanview.resort.dto.BillDTO;
import com.oceanview.resort.mapper.BillMapper;
import com.oceanview.resort.model.Bill;
import com.oceanview.resort.model.Guest;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.model.User;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Tests for BillMapper (Bill -> BillDTO).
 */
public class BillMapperTest {

    @Test
    public void toDTO_nullReturnsNull() {
        Assert.assertNull(BillMapper.toDTO(null));
    }

    @Test
    public void toDTO_mapsAllFields() {
        Bill bill = new Bill();
        bill.setId(1L);
        bill.setBillNo("BILL-001");
        bill.setNumberOfNights(3);
        bill.setRoomRate(new BigDecimal("15000.00"));
        bill.setTotalAmount(new BigDecimal("45000.00"));
        bill.setDiscountAmount(new BigDecimal("2000.00"));
        bill.setTaxAmount(new BigDecimal("4300.00"));
        bill.setNetAmount(new BigDecimal("47300.00"));
        bill.setGeneratedAt(LocalDateTime.of(2026, 2, 12, 10, 0));

        Reservation res = new Reservation();
        res.setReservationNo("RES-001");
        Guest guest = new Guest();
        guest.setFirstName("John");
        guest.setLastName("Doe");
        res.setGuest(guest);
        Room room = new Room();
        room.setRoomNumber("101");
        res.setRoom(room);
        bill.setReservation(res);

        BillDTO dto = BillMapper.toDTO(bill);

        Assert.assertNotNull(dto);
        Assert.assertEquals(1L, dto.getId());
        Assert.assertEquals("BILL-001", dto.getBillNo());
        Assert.assertEquals("RES-001", dto.getReservationNo());
        Assert.assertEquals("John Doe", dto.getGuestName());
        Assert.assertEquals("101", dto.getRoomNumber());
        Assert.assertEquals(3, dto.getNumberOfNights());
        Assert.assertEquals("15000.00", dto.getRoomRate());
        Assert.assertEquals("45000.00", dto.getTotalAmount());
        Assert.assertEquals("2000.00", dto.getDiscountAmount());
        Assert.assertEquals("4300.00", dto.getTaxAmount());
        Assert.assertEquals("47300.00", dto.getNetAmount());
        Assert.assertEquals("2026-02-12 10:00", dto.getGeneratedAt());
    }

    @Test
    public void toDTO_nullReservation_doesNotSetReservationFields() {
        Bill bill = new Bill();
        bill.setId(2L);
        bill.setBillNo("BILL-002");
        bill.setNumberOfNights(2);
        bill.setReservation(null);

        BillDTO dto = BillMapper.toDTO(bill);

        Assert.assertNotNull(dto);
        Assert.assertEquals(2L, dto.getId());
        Assert.assertNull(dto.getReservationNo());
        Assert.assertNull(dto.getGuestName());
        Assert.assertNull(dto.getRoomNumber());
    }

    @Test
    public void toDTO_nullBigDecimals_mapsToNull() {
        Bill bill = new Bill();
        bill.setId(3L);
        bill.setRoomRate(null);
        bill.setTotalAmount(null);
        bill.setDiscountAmount(null);
        bill.setTaxAmount(null);
        bill.setNetAmount(null);
        bill.setGeneratedAt(null);

        BillDTO dto = BillMapper.toDTO(bill);

        Assert.assertNull(dto.getRoomRate());
        Assert.assertNull(dto.getTotalAmount());
        Assert.assertNull(dto.getDiscountAmount());
        Assert.assertNull(dto.getTaxAmount());
        Assert.assertNull(dto.getNetAmount());
        Assert.assertNull(dto.getGeneratedAt());
    }
}
