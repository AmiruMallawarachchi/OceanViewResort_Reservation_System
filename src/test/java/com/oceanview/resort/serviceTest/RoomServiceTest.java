package com.oceanview.resort.serviceTest;

import com.oceanview.resort.dto.RoomDTO;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.repository.RoomRepository;
import com.oceanview.resort.service.impl.RoomServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RoomServiceTest {

    @Mock
    private RoomRepository repository;

    private RoomServiceImpl roomService;

    @Before
    public void setup() {
        roomService = new RoomServiceImpl(repository);
    }

    @Test
    public void testCreate() {
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
        assertEquals("101", result.getRoomNumber());
        verify(repository).create(any(Room.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateBlankRoomNumberThrows() {
        RoomDTO dto = new RoomDTO();
        dto.setRoomNumber("   ");
        roomService.create(dto);
    }

    @Test
    public void testUpdate() {
        Room updated = new Room();
        updated.setId(1L);
        updated.setRoomNumber("101");
        when(repository.update(any(Room.class))).thenReturn(updated);

        RoomDTO dto = new RoomDTO();
        dto.setId(1L);
        dto.setRoomNumber("101");
        RoomDTO result = roomService.update(dto);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(repository).update(any(Room.class));
    }

    @Test
    public void testDelete() {
        when(repository.delete(5L)).thenReturn(true);
        assertTrue(roomService.delete(5L));
        verify(repository).delete(5L);
    }

    @Test
    public void testFindById() {
        Room room = new Room();
        room.setId(2L);
        room.setRoomNumber("201");
        when(repository.findById(2L)).thenReturn(room);

        RoomDTO result = roomService.findById(2L);
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("201", result.getRoomNumber());
    }

    @Test
    public void testFindByIdNotFound() {
        when(repository.findById(999L)).thenReturn(null);
        RoomDTO result = roomService.findById(999L);
        assertNull(result);
    }

    @Test
    public void testFindAll() {
        Room r1 = new Room();
        r1.setId(1L);
        Room r2 = new Room();
        r2.setId(2L);
        when(repository.findAll()).thenReturn(Arrays.asList(r1, r2));

        List<RoomDTO> result = roomService.findAll();
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repository).findAll();
    }

    @Test
    public void testFindAvailable() {
        Room r = new Room();
        r.setId(1L);
        r.setRoomNumber("101");
        LocalDate checkIn = LocalDate.of(2026, 2, 15);
        LocalDate checkOut = LocalDate.of(2026, 2, 18);
        when(repository.findAvailable(eq(checkIn), eq(checkOut))).thenReturn(Arrays.asList(r));

        List<RoomDTO> result = roomService.findAvailable(checkIn, checkOut);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("101", result.get(0).getRoomNumber());
        verify(repository).findAvailable(checkIn, checkOut);
    }
}
