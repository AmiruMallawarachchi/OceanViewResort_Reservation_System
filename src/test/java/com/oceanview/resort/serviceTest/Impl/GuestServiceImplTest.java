package com.oceanview.resort.serviceTest.Impl;

import com.oceanview.resort.dto.GuestDTO;
import com.oceanview.resort.model.Guest;
import com.oceanview.resort.repository.GuestRepository;
import com.oceanview.resort.service.impl.GuestServiceImpl;
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
public class GuestServiceImplTest {

    @Mock
    private GuestRepository guestRepository;

    private GuestServiceImpl guestService;

    @Before
    public void setup() {
        guestService = new GuestServiceImpl(guestRepository);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateGuest_blankFullName_throws() {
        GuestDTO dto = new GuestDTO();
        dto.setFullName("   ");
        guestService.createGuest(dto);
    }

    @Test
    public void testCreateGuest_valid_callsRepositoryAndReturnsDTO() {
        Guest saved = new Guest();
        saved.setId(1L);
        saved.setFirstName("John");
        saved.setLastName("Doe");
        saved.setEmail("john@test.com");
        saved.setIdNumber("ID1");
        when(guestRepository.create(any(Guest.class))).thenReturn(saved);

        GuestDTO dto = new GuestDTO();
        dto.setFullName("John Doe");
        dto.setEmail("john@test.com");
        dto.setIdNumber("ID1");

        GuestDTO result = guestService.createGuest(dto);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(guestRepository).create(any(Guest.class));
    }

    @Test
    public void testUpdateGuest_delegatesToRepository() {
        Guest updated = new Guest();
        updated.setId(1L);
        updated.setFirstName("Jane");
        updated.setLastName("Doe");
        when(guestRepository.update(any(Guest.class))).thenReturn(updated);

        GuestDTO dto = new GuestDTO();
        dto.setId(1L);
        dto.setFullName("Jane Doe");
        GuestDTO result = guestService.updateGuest(dto);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(guestRepository).update(any(Guest.class));
    }

    @Test
    public void testDeleteGuest_delegatesToRepository() {
        when(guestRepository.delete(5L)).thenReturn(true);
        assertTrue(guestService.deleteGuest(5L));
        verify(guestRepository).delete(5L);
    }

    @Test
    public void testFindById_nullFromRepository_returnsNull() {
        when(guestRepository.findById(999L)).thenReturn(null);
        GuestDTO result = guestService.findById(999L);
        assertNull(result);
    }
}
