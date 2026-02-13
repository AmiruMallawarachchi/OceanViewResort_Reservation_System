package com.oceanview.resort.mapper;

import com.oceanview.resort.dto.RoomTypeDTO;
import com.oceanview.resort.model.RoomType;

import java.math.BigDecimal;

public final class RoomTypeMapper {
    private RoomTypeMapper() {
    }

    public static RoomTypeDTO toDTO(RoomType roomType) {
        if (roomType == null) {
            return null;
        }
        RoomTypeDTO dto = new RoomTypeDTO();
        dto.setId(roomType.getId());
        dto.setTypeName(roomType.getTypeName());
        dto.setDescription(roomType.getDescription());
        dto.setRatePerNight(roomType.getRatePerNight() == null ? null : roomType.getRatePerNight().toPlainString());
        dto.setMaxOccupancy(roomType.getMaxOccupancy());
        dto.setAmenities(roomType.getAmenities());
        dto.setActive(roomType.isActive());
        return dto;
    }

    public static RoomType toEntity(RoomTypeDTO dto) {
        if (dto == null) {
            return null;
        }
        RoomType roomType = new RoomType();
        roomType.setId(dto.getId());
        roomType.setTypeName(dto.getTypeName());
        roomType.setDescription(dto.getDescription());
        if (dto.getRatePerNight() != null && !dto.getRatePerNight().isBlank()) {
            roomType.setRatePerNight(new BigDecimal(dto.getRatePerNight()));
        }
        roomType.setMaxOccupancy(dto.getMaxOccupancy());
        roomType.setAmenities(dto.getAmenities());
        roomType.setActive(dto.isActive());
        return roomType;
    }
}
