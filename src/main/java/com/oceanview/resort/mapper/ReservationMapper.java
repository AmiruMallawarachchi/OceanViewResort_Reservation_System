package com.oceanview.resort.mapper;

import com.oceanview.resort.dto.ReservationDTO;
import com.oceanview.resort.model.Guest;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.enums.ReservationStatus;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.model.User;
import com.oceanview.resort.util.DateUtil;

public final class ReservationMapper {
    private ReservationMapper() {
    }

    public static ReservationDTO toDTO(Reservation reservation) {
        if (reservation == null) {
            return null;
        }
        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        dto.setReservationNo(reservation.getReservationNo());
        if (reservation.getGuest() != null) {
            dto.setGuestId(reservation.getGuest().getId());
            dto.setGuestName(reservation.getGuest().getFullName());
            dto.setGuestEmail(reservation.getGuest().getEmail());
            dto.setGuestPhone(reservation.getGuest().getPhone());
            dto.setGuestAddress(reservation.getGuest().getAddress());
            dto.setGuestType(reservation.getGuest().getGuestType() == null ? null : reservation.getGuest().getGuestType().name());
        }
        if (reservation.getRoom() != null) {
            dto.setRoomId(reservation.getRoom().getId());
            dto.setRoomNumber(reservation.getRoom().getRoomNumber());
            if (reservation.getRoom().getRoomType() != null) {
                dto.setRoomTypeName(reservation.getRoom().getRoomType().getTypeName());
            }
        }
        dto.setCheckInDate(DateUtil.formatDate(reservation.getCheckInDate()));
        dto.setCheckOutDate(DateUtil.formatDate(reservation.getCheckOutDate()));
        dto.setStatus(reservation.getStatus() == null ? null : reservation.getStatus().name());
        dto.setCreatedBy(reservation.getCreatedBy() == null ? null : reservation.getCreatedBy().getUsername());
        dto.setCreatedAt(DateUtil.formatDateTime(reservation.getCreatedAt()));
        return dto;
    }

    public static Reservation toEntity(ReservationDTO dto) {
        if (dto == null) {
            return null;
        }
        Reservation reservation = new Reservation();
        reservation.setId(dto.getId());
        reservation.setReservationNo(dto.getReservationNo());

        if (dto.getGuestId() > 0) {
            Guest guest = new Guest();
            guest.setId(dto.getGuestId());
            reservation.setGuest(guest);
        }
        if (dto.getRoomId() > 0) {
            Room room = new Room();
            room.setId(dto.getRoomId());
            reservation.setRoom(room);
        }
        reservation.setCheckInDate(DateUtil.parseDate(dto.getCheckInDate()));
        reservation.setCheckOutDate(DateUtil.parseDate(dto.getCheckOutDate()));
        if (dto.getStatus() != null) {
            reservation.setStatus(ReservationStatus.valueOf(dto.getStatus()));
        }
        return reservation;
    }

    public static Reservation toEntity(ReservationDTO dto, User createdBy) {
        Reservation reservation = toEntity(dto);
        if (reservation != null) {
            reservation.setCreatedBy(createdBy);
        }
        return reservation;
    }
}
