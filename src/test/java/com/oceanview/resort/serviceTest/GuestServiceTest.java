package com.oceanview.resort.serviceTest;

import com.oceanview.resort.dto.GuestDTO;
import com.oceanview.resort.model.Guest;
import com.oceanview.resort.repository.GuestRepository;
import com.oceanview.resort.service.impl.GuestServiceImpl;
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
public class GuestServiceTest {

    @Mock
    private GuestRepository guestRepository;

    private GuestServiceImpl guestService;

    @Before
    public void setup() {
        guestService = new GuestServiceImpl(guestRepository);
    }

    @Test
    public void testCreateGuest() {
        Guest created = new Guest();
        created.setId(1L);
        created.setFirstName("John");
        created.setLastName("Doe");
        created.setEmail("john@test.com");
        created.setIdNumber("ID123");
        when(guestRepository.create(any(Guest.class))).thenReturn(created);

        GuestDTO dto = new GuestDTO();
        dto.setFullName("John Doe");
        dto.setEmail("john@test.com");
        dto.setIdNumber("ID123");

        GuestDTO result = guestService.createGuest(dto);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getFullName());
        verify(guestRepository).create(any(Guest.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateGuestBlankNameThrows() {
        GuestDTO dto = new GuestDTO();
        dto.setFullName("   ");
        guestService.createGuest(dto);
    }

    @Test
    public void testUpdateGuest() {
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
    public void testDeleteGuest() {
        when(guestRepository.delete(5L)).thenReturn(true);
        assertTrue(guestService.deleteGuest(5L));
        verify(guestRepository).delete(5L);
    }

    @Test
    public void testFindById() {
        Guest g = new Guest();
        g.setId(2L);
        g.setFirstName("Find");
        g.setLastName("Me");
        when(guestRepository.findById(2L)).thenReturn(g);

        GuestDTO result = guestService.findById(2L);
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("Find Me", result.getFullName());
    }

    @Test
    public void testFindByIdNotFound() {
        when(guestRepository.findById(999L)).thenReturn(null);
        GuestDTO result = guestService.findById(999L);
        assertNull(result);
    }

    @Test
    public void testFindByIdNumber() {
        Guest g = new Guest();
        g.setId(3L);
        g.setIdNumber("ID456");
        when(guestRepository.findByIdNumber("ID456")).thenReturn(g);

        GuestDTO result = guestService.findByIdNumber("ID456");
        assertNotNull(result);
        assertEquals("ID456", result.getIdNumber());
    }

    @Test
    public void testFindAll() {
        Guest g1 = new Guest();
        g1.setId(1L);
        Guest g2 = new Guest();
        g2.setId(2L);
        when(guestRepository.findAll()).thenReturn(Arrays.asList(g1, g2));

        List<GuestDTO> result = guestService.findAll();
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(guestRepository).findAll();
    }
}
