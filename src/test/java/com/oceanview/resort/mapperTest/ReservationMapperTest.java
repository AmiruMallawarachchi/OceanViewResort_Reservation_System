package com.oceanview.resort.mapperTest;

import com.oceanview.resort.dto.ReservationDTO;
import com.oceanview.resort.mapper.ReservationMapper;
import com.oceanview.resort.model.Guest;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.model.RoomType;
import com.oceanview.resort.model.User;
import com.oceanview.resort.model.enums.ReservationStatus;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Tests for ReservationMapper (Reservation <-> ReservationDTO).
 */
public class ReservationMapperTest {

    @Test
    public void toDTO_nullReturnsNull() {
        Assert.assertNull(ReservationMapper.toDTO(null));
    }

    @Test
    public void toDTO_mapsAllFields() {
        Reservation res = new Reservation();
        res.setId(1L);
        res.setReservationNo("RES-001");
        Guest guest = new Guest();
        guest.setId(10L);
        guest.setFirstName("John");
        guest.setLastName("Doe");
        guest.setGuestType(com.oceanview.resort.model.enums.GuestType.REGULAR);
        res.setGuest(guest);
        Room room = new Room();
        room.setId(20L);
        room.setRoomNumber("101");
        RoomType rt = new RoomType();
        rt.setTypeName("Deluxe");
        room.setRoomType(rt);
        res.setRoom(room);
        res.setCheckInDate(LocalDate.of(2026, 2, 12));
        res.setCheckOutDate(LocalDate.of(2026, 2, 14));
        res.setStatus(ReservationStatus.PENDING);
        User user = new User();
        user.setUsername("admin");
        res.setCreatedBy(user);
        res.setCreatedAt(LocalDateTime.of(2026, 2, 1, 10, 0));

        ReservationDTO dto = ReservationMapper.toDTO(res);

        Assert.assertNotNull(dto);
        Assert.assertEquals(1L, dto.getId());
        Assert.assertEquals("RES-001", dto.getReservationNo());
        Assert.assertEquals(10L, dto.getGuestId());
        Assert.assertEquals("John Doe", dto.getGuestName());
        Assert.assertEquals(20L, dto.getRoomId());
        Assert.assertEquals("101", dto.getRoomNumber());
        Assert.assertEquals("Deluxe", dto.getRoomTypeName());
        Assert.assertEquals("2026-02-12", dto.getCheckInDate());
        Assert.assertEquals("2026-02-14", dto.getCheckOutDate());
        Assert.assertEquals("PENDING", dto.getStatus());
        Assert.assertEquals("admin", dto.getCreatedBy());
        Assert.assertEquals("2026-02-01 10:00", dto.getCreatedAt());
    }

    @Test
    public void toEntity_nullReturnsNull() {
        Assert.assertNull(ReservationMapper.toEntity(null));
    }

    @Test
    public void toEntity_mapsDtoToReservation() {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(2L);
        dto.setReservationNo("RES-002");
        dto.setGuestId(11L);
        dto.setRoomId(21L);
        dto.setCheckInDate("2026-03-01");
        dto.setCheckOutDate("2026-03-03");
        dto.setStatus("CONFIRMED");

        Reservation res = ReservationMapper.toEntity(dto);

        Assert.assertNotNull(res);
        Assert.assertEquals(2L, res.getId());
        Assert.assertEquals("RES-002", res.getReservationNo());
        Assert.assertEquals(11L, res.getGuest().getId());
        Assert.assertEquals(21L, res.getRoom().getId());
        Assert.assertEquals(LocalDate.of(2026, 3, 1), res.getCheckInDate());
        Assert.assertEquals(LocalDate.of(2026, 3, 3), res.getCheckOutDate());
        Assert.assertEquals(ReservationStatus.CONFIRMED, res.getStatus());
    }

    @Test
    public void toEntity_withCreatedBy_setsUser() {
        ReservationDTO dto = new ReservationDTO();
        dto.setGuestId(1L);
        dto.setRoomId(1L);
        dto.setCheckInDate("2026-02-10");
        dto.setCheckOutDate("2026-02-11");
        User user = new User();
        user.setId(5L);
        user.setUsername("res1");

        Reservation res = ReservationMapper.toEntity(dto, user);

        Assert.assertNotNull(res);
        Assert.assertNotNull(res.getCreatedBy());
        Assert.assertEquals(5L, res.getCreatedBy().getId());
        Assert.assertEquals("res1", res.getCreatedBy().getUsername());
    }

    @Test
    public void toEntity_nullDtoWithUserReturnsNull() {
        User user = new User();
        Assert.assertNull(ReservationMapper.toEntity(null, user));
    }
}
