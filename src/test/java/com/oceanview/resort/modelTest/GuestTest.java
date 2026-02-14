package com.oceanview.resort.modelTest;

import com.oceanview.resort.model.Guest;
import com.oceanview.resort.model.enums.GuestType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

public class GuestTest {

    private Guest guest;

    @Before
    public void setUp() {
        guest = new Guest();
    }

    @Test
    public void testDefaultValues() {
        Assert.assertEquals(0L, guest.getId());
        Assert.assertNull(guest.getFirstName());
        Assert.assertNull(guest.getLastName());
        Assert.assertNull(guest.getEmail());
        Assert.assertNull(guest.getPhone());
        Assert.assertNull(guest.getAddress());
        Assert.assertNull(guest.getIdType());
        Assert.assertNull(guest.getIdNumber());
        Assert.assertNull(guest.getNationality());
        Assert.assertNull(guest.getGuestType());
        Assert.assertNull(guest.getCreatedAt());
    }

    @Test
    public void testGettersAndSetters() {
        LocalDateTime now = LocalDateTime.now();

        guest.setId(1L);
        guest.setFirstName("John");
        guest.setLastName("Doe");
        guest.setEmail("john@example.com");
        guest.setPhone("+1234567890");
        guest.setAddress("123 Main St");
        guest.setIdType("PASSPORT");
        guest.setIdNumber("AB123456");
        guest.setNationality("US");
        guest.setGuestType(GuestType.REGULAR);
        guest.setCreatedAt(now);

        Assert.assertEquals(1L, guest.getId());
        Assert.assertEquals("John", guest.getFirstName());
        Assert.assertEquals("Doe", guest.getLastName());
        Assert.assertEquals("john@example.com", guest.getEmail());
        Assert.assertEquals("+1234567890", guest.getPhone());
        Assert.assertEquals("123 Main St", guest.getAddress());
        Assert.assertEquals("PASSPORT", guest.getIdType());
        Assert.assertEquals("AB123456", guest.getIdNumber());
        Assert.assertEquals("US", guest.getNationality());
        Assert.assertEquals(GuestType.REGULAR, guest.getGuestType());
        Assert.assertEquals(now, guest.getCreatedAt());
    }

    @Test
    public void testGetFullName() {
        guest.setFirstName("Jane");
        guest.setLastName("Smith");
        Assert.assertEquals("Jane Smith", guest.getFullName());
    }

    @Test
    public void testGetFullName_nullFirst_returnsSpaceAndLast() {
        guest.setFirstName(null);
        guest.setLastName("Doe");
        Assert.assertEquals(" Doe", guest.getFullName());
    }

    @Test
    public void testGetFullName_nullLast_returnsFirstAndSpace() {
        guest.setFirstName("John");
        guest.setLastName(null);
        Assert.assertEquals("John ", guest.getFullName());
    }

    @Test
    public void testGetFullName_bothNull_returnsEmptySpace() {
        guest.setFirstName(null);
        guest.setLastName(null);
        Assert.assertEquals(" ", guest.getFullName());
    }

    @Test
    public void testConstructorWithArgs() {
        LocalDateTime now = LocalDateTime.now();
        Guest g = new Guest(2L, "A", "B", "a@b.com", "1", "addr", "ID", "123", "UK", GuestType.VIP, now);
        Assert.assertEquals(2L, g.getId());
        Assert.assertEquals("A", g.getFirstName());
        Assert.assertEquals("B", g.getLastName());
        Assert.assertEquals(GuestType.VIP, g.getGuestType());
        Assert.assertEquals("A B", g.getFullName());
    }
}
