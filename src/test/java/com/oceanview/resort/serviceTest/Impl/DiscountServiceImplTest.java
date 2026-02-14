package com.oceanview.resort.serviceTest.Impl;

import com.oceanview.resort.dto.DiscountDTO;
import com.oceanview.resort.model.Discount;
import com.oceanview.resort.repository.DiscountRepository;
import com.oceanview.resort.service.impl.DiscountServiceImpl;
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
public class DiscountServiceImplTest {

    @Mock
    private DiscountRepository repository;

    private DiscountServiceImpl discountService;

    @Before
    public void setup() {
        discountService = new DiscountServiceImpl(repository);
    }

    @Test
    public void testCreate_mapsDtoToEntityAndBack() {
        Discount created = new Discount();
        created.setId(1L);
        created.setName("Sale");
        created.setActive(true);
        when(repository.create(any(Discount.class))).thenReturn(created);

        DiscountDTO dto = new DiscountDTO();
        dto.setName("Sale");
        dto.setDiscountType("PROMOTION");
        dto.setPercent("10");
        dto.setActive(true);

        DiscountDTO result = discountService.create(dto);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Sale", result.getName());
        verify(repository).create(any(Discount.class));
    }

    @Test
    public void testUpdate_delegatesToRepository() {
        Discount updated = new Discount();
        updated.setId(1L);
        when(repository.update(any(Discount.class))).thenReturn(updated);

        DiscountDTO dto = new DiscountDTO();
        dto.setId(1L);
        dto.setName("Updated");
        DiscountDTO result = discountService.update(dto);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(repository).update(any(Discount.class));
    }

    @Test
    public void testFindById_null_returnsNullDto() {
        when(repository.findById(999L)).thenReturn(null);
        DiscountDTO result = discountService.findById(999L);
        assertNull(result);
    }
}
