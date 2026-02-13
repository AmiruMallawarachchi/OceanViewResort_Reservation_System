package com.oceanview.resort.mapper;

import com.oceanview.resort.dto.RoomDTO;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.model.enums.RoomStatus;
import com.oceanview.resort.model.RoomType;

public final class RoomMapper {
    private RoomMapper() {
    }

    public static RoomDTO toDTO(Room room) {
        if (room == null) {
            return null;
        }
        RoomDTO dto = new RoomDTO();
        dto.setId(room.getId());
        dto.setRoomNumber(room.getRoomNumber());
        if (room.getRoomType() != null) {
            dto.setRoomTypeId(room.getRoomType().getId());
            dto.setRoomTypeName(room.getRoomType().getTypeName());
            if (room.getRoomType().getRatePerNight() != null) {
                dto.setRoomTypeRatePerNight(room.getRoomType().getRatePerNight().toPlainString());
            }
            dto.setRoomTypeMaxOccupancy(room.getRoomType().getMaxOccupancy());
            dto.setRoomTypeAmenities(room.getRoomType().getAmenities());
        }
        dto.setFloor(room.getFloor());
        dto.setStatus(room.getStatus() == null ? null : room.getStatus().name());
        dto.setDescription(room.getDescription());
        dto.setFullAccess(room.isFullAccess());
        return dto;
    }

    public static Room toEntity(RoomDTO dto) {
        if (dto == null) {
            return null;
        }
        Room room = new Room();
        room.setId(dto.getId());
        room.setRoomNumber(dto.getRoomNumber());
        if (dto.getRoomTypeId() > 0) {
            RoomType roomType = new RoomType();
            roomType.setId(dto.getRoomTypeId());
            room.setRoomType(roomType);
        }
        room.setFloor(dto.getFloor());
        if (dto.getStatus() != null) {
            room.setStatus(RoomStatus.valueOf(dto.getStatus()));
        }
        room.setDescription(dto.getDescription());
        room.setFullAccess(dto.isFullAccess());
        return room;
    }
}
