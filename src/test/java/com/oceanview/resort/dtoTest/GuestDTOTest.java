package com.oceanview.resort.dtoTest;

import com.oceanview.resort.dto.GuestDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for GuestDTO getters and setters.
 */
public class GuestDTOTest {

    private GuestDTO dto;

    @Before
    public void setup() {
        dto = new GuestDTO();
    }

    @Test
    public void testDefaultValues() {
        Assert.assertEquals(0L, dto.getId());
        Assert.assertNull(dto.getFullName());
        Assert.assertNull(dto.getEmail());
        Assert.assertNull(dto.getPhone());
        Assert.assertNull(dto.getAddress());
        Assert.assertNull(dto.getIdType());
        Assert.assertNull(dto.getIdNumber());
        Assert.assertNull(dto.getNationality());
        Assert.assertNull(dto.getGuestType());
    }

    @Test
    public void testSetAndGetAllFields() {
        dto.setId(1L);
        dto.setFullName("Jane Smith");
        dto.setEmail("jane@example.com");
        dto.setPhone("0771234567");
        dto.setAddress("456 Beach Rd");
        dto.setIdType("PASSPORT");
        dto.setIdNumber("P123456");
        dto.setNationality("UK");
        dto.setGuestType("REGULAR");

        Assert.assertEquals(1L, dto.getId());
        Assert.assertEquals("Jane Smith", dto.getFullName());
        Assert.assertEquals("jane@example.com", dto.getEmail());
        Assert.assertEquals("0771234567", dto.getPhone());
        Assert.assertEquals("456 Beach Rd", dto.getAddress());
        Assert.assertEquals("PASSPORT", dto.getIdType());
        Assert.assertEquals("P123456", dto.getIdNumber());
        Assert.assertEquals("UK", dto.getNationality());
        Assert.assertEquals("REGULAR", dto.getGuestType());
    }

    @Test
    public void testUpdateFields() {
        dto.setId(1L);
        dto.setFullName("Old Name");
        dto.setGuestType("VIP");

        dto.setId(2L);
        dto.setFullName("New Name");
        dto.setGuestType("CORPORATE");

        Assert.assertEquals(2L, dto.getId());
        Assert.assertEquals("New Name", dto.getFullName());
        Assert.assertEquals("CORPORATE", dto.getGuestType());
    }
}
