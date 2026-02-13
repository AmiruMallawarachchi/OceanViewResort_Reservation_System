package com.oceanview.resort.serviceTest.Impl;

import com.oceanview.resort.dto.RoomDTO;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.repository.RoomRepository;
import com.oceanview.resort.service.impl.RoomServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RoomServiceImplTest {

    @Mock
    private RoomRepository repository;

    private RoomServiceImpl roomService;

    @Before
    public void setup() {
        roomService = new RoomServiceImpl(repository);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreate_blankRoomNumber_throws() {
        RoomDTO dto = new RoomDTO();
        dto.setRoomNumber("   ");
        roomService.create(dto);
    }

    @Test
    public void testCreate_valid_callsRepository() {
        Room created = new Room();
        created.setId(1L);
        created.setRoomNumber("101");
        when(repository.create(any(Room.class))).thenReturn(created);

        RoomDTO dto = new RoomDTO();
        dto.setRoomNumber("101");
        dto.setRoomTypeId(1L);
        dto.setFloor(1);
        RoomDTO result = roomService.create(dto);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(repository).create(any(Room.class));
    }

    @Test
    public void testFindById_null_returnsNull() {
        when(repository.findById(999L)).thenReturn(null);
        RoomDTO result = roomService.findById(999L);
        assertNull(result);
    }
}
