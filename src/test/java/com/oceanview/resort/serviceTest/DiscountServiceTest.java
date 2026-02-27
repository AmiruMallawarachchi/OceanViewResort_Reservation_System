package com.oceanview.resort.serviceTest;

import com.oceanview.resort.dto.DiscountDTO;
import com.oceanview.resort.model.Discount;
import com.oceanview.resort.repository.DiscountRepository;
import com.oceanview.resort.service.impl.DiscountServiceImpl;
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
public class DiscountServiceTest {

    @Mock
    private DiscountRepository repository;

    private DiscountServiceImpl discountService;

    @Before
    public void setup() {
        discountService = new DiscountServiceImpl(repository);
    }

    @Test
    public void testCreate() {
        Discount created = new Discount();
        created.setId(1L);
        created.setName("Summer Sale");
        created.setActive(true);
        when(repository.create(any(Discount.class))).thenReturn(created);

        DiscountDTO dto = new DiscountDTO();
        dto.setName("Summer Sale");
        dto.setActive(true);

        DiscountDTO result = discountService.create(dto);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Summer Sale", result.getName());
        verify(repository).create(any(Discount.class));
    }

    @Test
    public void testUpdate() {
        Discount updated = new Discount();
        updated.setId(1L);
        updated.setName("Updated Sale");
        when(repository.update(any(Discount.class))).thenReturn(updated);

        DiscountDTO dto = new DiscountDTO();
        dto.setId(1L);
        dto.setName("Updated Sale");
        DiscountDTO result = discountService.update(dto);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(repository).update(any(Discount.class));
    }

    @Test
    public void testDelete() {
        when(repository.delete(3L)).thenReturn(true);
        assertTrue(discountService.delete(3L));
        verify(repository).delete(3L);
    }

    @Test
    public void testFindById() {
        Discount d = new Discount();
        d.setId(2L);
        d.setName("Find Me");
        when(repository.findById(2L)).thenReturn(d);

        DiscountDTO result = discountService.findById(2L);
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("Find Me", result.getName());
    }

    @Test
    public void testFindByIdNotFound() {
        when(repository.findById(999L)).thenReturn(null);
        DiscountDTO result = discountService.findById(999L);
        assertNull(result);
    }

    @Test
    public void testFindAll() {
        Discount d1 = new Discount();
        d1.setId(1L);
        Discount d2 = new Discount();
        d2.setId(2L);
        when(repository.findAll()).thenReturn(Arrays.asList(d1, d2));

        List<DiscountDTO> result = discountService.findAll();
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repository).findAll();
    }

    @Test
    public void testFindActive() {
        Discount d = new Discount();
        d.setId(1L);
        d.setActive(true);
        when(repository.findActive()).thenReturn(List.of(d));

        List<DiscountDTO> result = discountService.findActive();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isActive());
        verify(repository).findActive();
    }
}
