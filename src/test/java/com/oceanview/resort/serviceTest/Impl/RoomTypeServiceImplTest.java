package com.oceanview.resort.serviceTest.Impl;

import com.oceanview.resort.dto.RoomTypeDTO;
import com.oceanview.resort.model.RoomType;
import com.oceanview.resort.repository.RoomTypeRepository;
import com.oceanview.resort.service.impl.RoomTypeServiceImpl;
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
public class RoomTypeServiceImplTest {

    @Mock
    private RoomTypeRepository repository;

    private RoomTypeServiceImpl roomTypeService;

    @Before
    public void setup() {
        roomTypeService = new RoomTypeServiceImpl(repository);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreate_blankTypeName_throws() {
        RoomTypeDTO dto = new RoomTypeDTO();
        dto.setTypeName("   ");
        roomTypeService.create(dto);
    }

    @Test
    public void testCreate_valid_callsRepository() {
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
        verify(repository).create(any(RoomType.class));
    }

    @Test
    public void testFindById_null_returnsNull() {
        when(repository.findById(999L)).thenReturn(null);
        RoomTypeDTO result = roomTypeService.findById(999L);
        assertNull(result);
    }
}
