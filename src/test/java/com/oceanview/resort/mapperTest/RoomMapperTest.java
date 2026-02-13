package com.oceanview.resort.mapperTest;

import com.oceanview.resort.dto.RoomDTO;
import com.oceanview.resort.mapper.RoomMapper;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.model.RoomType;
import com.oceanview.resort.model.enums.RoomStatus;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * Tests for RoomMapper (Room <-> RoomDTO).
 */
public class RoomMapperTest {

    @Test
    public void toDTO_nullReturnsNull() {
        Assert.assertNull(RoomMapper.toDTO(null));
    }

    @Test
    public void toDTO_mapsAllFields() {
        Room room = new Room();
        room.setId(1L);
        room.setRoomNumber("201");
        room.setFloor(2);
        room.setStatus(RoomStatus.AVAILABLE);
        room.setDescription("Ocean view");
        room.setFullAccess(true);

        RoomType rt = new RoomType();
        rt.setId(2L);
        rt.setTypeName("Deluxe");
        rt.setRatePerNight(new BigDecimal("15000.00"));
        rt.setMaxOccupancy(4);
        rt.setAmenities("Sea view, Mini bar");
        room.setRoomType(rt);

        RoomDTO dto = RoomMapper.toDTO(room);

        Assert.assertNotNull(dto);
        Assert.assertEquals(1L, dto.getId());
        Assert.assertEquals("201", dto.getRoomNumber());
        Assert.assertEquals(2L, dto.getRoomTypeId());
        Assert.assertEquals("Deluxe", dto.getRoomTypeName());
        Assert.assertEquals("15000.00", dto.getRoomTypeRatePerNight());
        Assert.assertEquals(4, dto.getRoomTypeMaxOccupancy());
        Assert.assertEquals("Sea view, Mini bar", dto.getRoomTypeAmenities());
        Assert.assertEquals(2, dto.getFloor());
        Assert.assertEquals("AVAILABLE", dto.getStatus());
        Assert.assertEquals("Ocean view", dto.getDescription());
        Assert.assertTrue(dto.isFullAccess());
    }

    @Test
    public void toDTO_nullRoomType_doesNotSetRoomTypeFields() {
        Room room = new Room();
        room.setId(2L);
        room.setRoomNumber("301");
        room.setRoomType(null);

        RoomDTO dto = RoomMapper.toDTO(room);

        Assert.assertNotNull(dto);
        Assert.assertEquals(0L, dto.getRoomTypeId());
        Assert.assertNull(dto.getRoomTypeName());
        Assert.assertNull(dto.getRoomTypeRatePerNight());
    }

    @Test
    public void toDTO_nullStatus_mapsToNull() {
        Room room = new Room();
        room.setId(3L);
        room.setStatus(null);

        RoomDTO dto = RoomMapper.toDTO(room);

        Assert.assertNull(dto.getStatus());
    }

    @Test
    public void toEntity_nullReturnsNull() {
        Assert.assertNull(RoomMapper.toEntity(null));
    }

    @Test
    public void toEntity_mapsAllFields() {
        RoomDTO dto = new RoomDTO();
        dto.setId(1L);
        dto.setRoomNumber("401");
        dto.setRoomTypeId(3L);
        dto.setFloor(4);
        dto.setStatus("OCCUPIED");
        dto.setDescription("Mountain view");
        dto.setFullAccess(false);

        Room room = RoomMapper.toEntity(dto);

        Assert.assertNotNull(room);
        Assert.assertEquals(1L, room.getId());
        Assert.assertEquals("401", room.getRoomNumber());
        Assert.assertNotNull(room.getRoomType());
        Assert.assertEquals(3L, room.getRoomType().getId());
        Assert.assertEquals(4, room.getFloor());
        Assert.assertEquals(RoomStatus.OCCUPIED, room.getStatus());
        Assert.assertEquals("Mountain view", room.getDescription());
        Assert.assertFalse(room.isFullAccess());
    }

    @Test
    public void toEntity_roomTypeIdZero_doesNotSetRoomType() {
        RoomDTO dto = new RoomDTO();
        dto.setId(2L);
        dto.setRoomTypeId(0);
        dto.setFloor(1);

        Room room = RoomMapper.toEntity(dto);

        Assert.assertNull(room.getRoomType());
    }

    @Test
    public void toEntity_nullStatus_leavesNull() {
        RoomDTO dto = new RoomDTO();
        dto.setId(3L);
        dto.setRoomTypeId(1L);
        dto.setFloor(1);
        dto.setStatus(null);

        Room room = RoomMapper.toEntity(dto);

        Assert.assertNull(room.getStatus());
    }
}
