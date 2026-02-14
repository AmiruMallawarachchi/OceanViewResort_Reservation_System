package com.oceanview.resort.mapperTest;

import com.oceanview.resort.dto.GuestDTO;
import com.oceanview.resort.mapper.GuestMapper;
import com.oceanview.resort.model.Guest;
import com.oceanview.resort.model.enums.GuestType;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for GuestMapper (Guest <-> GuestDTO).
 */
public class GuestMapperTest {

    @Test
    public void toDTO_nullReturnsNull() {
        Assert.assertNull(GuestMapper.toDTO(null));
    }

    @Test
    public void toDTO_mapsAllFields() {
        Guest guest = new Guest();
        guest.setId(1L);
        guest.setFirstName("John");
        guest.setLastName("Doe");
        guest.setEmail("john@test.com");
        guest.setPhone("555-1234");
        guest.setAddress("123 Main St");
        guest.setIdType("PASSPORT");
        guest.setIdNumber("ID123");
        guest.setNationality("US");
        guest.setGuestType(GuestType.VIP);

        GuestDTO dto = GuestMapper.toDTO(guest);

        Assert.assertNotNull(dto);
        Assert.assertEquals(1L, dto.getId());
        Assert.assertEquals("John Doe", dto.getFullName());
        Assert.assertEquals("john@test.com", dto.getEmail());
        Assert.assertEquals("555-1234", dto.getPhone());
        Assert.assertEquals("123 Main St", dto.getAddress());
        Assert.assertEquals("PASSPORT", dto.getIdType());
        Assert.assertEquals("ID123", dto.getIdNumber());
        Assert.assertEquals("US", dto.getNationality());
        Assert.assertEquals("VIP", dto.getGuestType());
    }

    @Test
    public void toDTO_nullGuestTypeMapsNull() {
        Guest guest = new Guest();
        guest.setId(2L);
        guest.setFirstName("Jane");
        guest.setLastName("Doe");
        guest.setGuestType(null);

        GuestDTO dto = GuestMapper.toDTO(guest);

        Assert.assertNotNull(dto);
        Assert.assertNull(dto.getGuestType());
    }

    @Test
    public void toEntity_nullReturnsNull() {
        Assert.assertNull(GuestMapper.toEntity(null));
    }

    @Test
    public void toEntity_fullNameTwoParts_splitsFirstAndLast() {
        GuestDTO dto = new GuestDTO();
        dto.setId(3L);
        dto.setFullName("John Doe");
        dto.setEmail("j@x.com");
        dto.setPhone("111");
        dto.setAddress("Addr");
        dto.setGuestType("REGULAR");

        Guest guest = GuestMapper.toEntity(dto);

        Assert.assertNotNull(guest);
        Assert.assertEquals(3L, guest.getId());
        Assert.assertEquals("John", guest.getFirstName());
        Assert.assertEquals("Doe", guest.getLastName());
        Assert.assertEquals(GuestType.REGULAR, guest.getGuestType());
    }

    @Test
    public void toEntity_fullNameSinglePart_onlyFirstName() {
        GuestDTO dto = new GuestDTO();
        dto.setId(4L);
        dto.setFullName("Madonna");
        dto.setGuestType("VIP");

        Guest guest = GuestMapper.toEntity(dto);

        Assert.assertNotNull(guest);
        Assert.assertEquals("Madonna", guest.getFirstName());
        Assert.assertNull(guest.getLastName());
    }

    @Test
    public void toEntity_nullGuestTypeLeavesNull() {
        GuestDTO dto = new GuestDTO();
        dto.setId(5L);
        dto.setFullName("X Y");
        dto.setGuestType(null);

        Guest guest = GuestMapper.toEntity(dto);

        Assert.assertNotNull(guest);
        Assert.assertNull(guest.getGuestType());
    }
}
