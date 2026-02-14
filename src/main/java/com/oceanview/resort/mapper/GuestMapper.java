package com.oceanview.resort.mapper;

import com.oceanview.resort.dto.GuestDTO;
import com.oceanview.resort.model.Guest;
import com.oceanview.resort.model.enums.GuestType;

public final class GuestMapper {
    private GuestMapper() {
    }

    public static GuestDTO toDTO(Guest guest) {
        if (guest == null) {
            return null;
        }
        GuestDTO dto = new GuestDTO();
        dto.setId(guest.getId());
        dto.setFullName(guest.getFullName());
        dto.setEmail(guest.getEmail());
        dto.setPhone(guest.getPhone());
        dto.setAddress(guest.getAddress());
        dto.setIdType(guest.getIdType());
        dto.setIdNumber(guest.getIdNumber());
        dto.setNationality(guest.getNationality());
        dto.setGuestType(guest.getGuestType() == null ? null : guest.getGuestType().name());
        return dto;
    }

    public static Guest toEntity(GuestDTO dto) {
        if (dto == null) {
            return null;
        }
        Guest guest = new Guest();
        guest.setId(dto.getId());
        if (dto.getFullName() != null) {
            String[] parts = dto.getFullName().split(" ", 2);
            guest.setFirstName(parts[0]);
            if (parts.length > 1) {
                guest.setLastName(parts[1]);
            }
        }
        guest.setEmail(dto.getEmail());
        guest.setPhone(dto.getPhone());
        guest.setAddress(dto.getAddress());
        guest.setIdType(dto.getIdType());
        guest.setIdNumber(dto.getIdNumber());
        guest.setNationality(dto.getNationality());
        if (dto.getGuestType() != null) {
            guest.setGuestType(GuestType.valueOf(dto.getGuestType()));
        }
        return guest;
    }
}
