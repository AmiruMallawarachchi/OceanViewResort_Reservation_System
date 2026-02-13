package com.oceanview.resort.serviceTest;

import com.oceanview.resort.dto.RoomTypeDTO;
import com.oceanview.resort.model.RoomType;
import com.oceanview.resort.repository.RoomTypeRepository;
import com.oceanview.resort.service.impl.RoomTypeServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RoomTypeServiceTest {

    @Mock
    private RoomTypeRepository repository;

    private RoomTypeServiceImpl roomTypeService;

    @Before
    public void setup() {
        roomTypeService = new RoomTypeServiceImpl(repository);
    }

    @Test
    public void testCreate() {
        RoomType created = new RoomType();
        created.setId(1L);
        created.setTypeName("Deluxe");
        when(repository.create(any(RoomType.class))).thenReturn(created);

        RoomTypeDTO dto = new RoomTypeDTO();
        dto.setTypeName("Deluxe");
        dto.setMaxOccupancy(4);

        RoomTypeDTO result = roomTypeService.create(dto);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Deluxe", result.getTypeName());
        verify(repository).create(any(RoomType.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateBlankTypeNameThrows() {
        RoomTypeDTO dto = new RoomTypeDTO();
        dto.setTypeName("   ");
        roomTypeService.create(dto);
    }

    @Test
    public void testUpdate() {
        RoomType updated = new RoomType();
        updated.setId(1L);
        updated.setTypeName("Standard");
        when(repository.update(any(RoomType.class))).thenReturn(updated);

        RoomTypeDTO dto = new RoomTypeDTO();
        dto.setId(1L);
        dto.setTypeName("Standard");
        RoomTypeDTO result = roomTypeService.update(dto);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(repository).update(any(RoomType.class));
    }

    @Test
    public void testDelete() {
        when(repository.delete(3L)).thenReturn(true);
        assertTrue(roomTypeService.delete(3L));
        verify(repository).delete(3L);
    }

    @Test
    public void testFindById() {
        RoomType rt = new RoomType();
        rt.setId(2L);
        rt.setTypeName("Suite");
        when(repository.findById(2L)).thenReturn(rt);

        RoomTypeDTO result = roomTypeService.findById(2L);
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("Suite", result.getTypeName());
    }

    @Test
    public void testFindByIdNotFound() {
        when(repository.findById(999L)).thenReturn(null);
        RoomTypeDTO result = roomTypeService.findById(999L);
        assertNull(result);
    }

    @Test
    public void testFindAll() {
        RoomType rt1 = new RoomType();
        rt1.setId(1L);
        RoomType rt2 = new RoomType();
        rt2.setId(2L);
        when(repository.findAll()).thenReturn(Arrays.asList(rt1, rt2));

        List<RoomTypeDTO> result = roomTypeService.findAll();
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repository).findAll();
    }
}
