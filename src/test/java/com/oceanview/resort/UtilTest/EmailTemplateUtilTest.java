package com.oceanview.resort.utilTest;

import com.oceanview.resort.messaging.ReservationEmailEvent;
import com.oceanview.resort.util.EmailTemplateUtil;
import org.junit.Assert;
import org.junit.Test;

public class EmailTemplateUtilTest {

    @Test
    public void testBuildConfirmationSubject() {
        ReservationEmailEvent event = new ReservationEmailEvent();
        String subject = EmailTemplateUtil.buildConfirmationSubject(event);
        Assert.assertEquals("OceanView Resort - Reservation Confirmation", subject);
    }

    @Test
    public void testBuildCancellationSubject() {
        ReservationEmailEvent event = new ReservationEmailEvent();
        String subject = EmailTemplateUtil.buildCancellationSubject(event);
        Assert.assertEquals("OceanView Resort - Reservation Cancelled", subject);
    }

    @Test
    public void testBuildConfirmationBody_includesGuestName() {
        ReservationEmailEvent event = new ReservationEmailEvent();
        event.setGuestName("John Doe");
        event.setReservationNo("RES-001");
        event.setRoomNumber("101");
        event.setRoomTypeName("Deluxe");
        event.setCheckInDate("2026-02-15");
        event.setCheckOutDate("2026-02-18");
        event.setStatus("CONFIRMED");

        String body = EmailTemplateUtil.buildConfirmationBody(event);
        Assert.assertNotNull(body);
        Assert.assertTrue(body.contains("Dear John Doe"));
        Assert.assertTrue(body.contains("Reservation No: RES-001"));
        Assert.assertTrue(body.contains("Room: 101"));
        Assert.assertTrue(body.contains("Room Type: Deluxe"));
        Assert.assertTrue(body.contains("Check-in: 2026-02-15"));
        Assert.assertTrue(body.contains("Check-out: 2026-02-18"));
        Assert.assertTrue(body.contains("Status: CONFIRMED"));
        Assert.assertTrue(body.contains("Thank you for making a reservation"));
        Assert.assertTrue(body.contains("OceanView Resort"));
    }

    @Test
    public void testBuildConfirmationBody_nullGuestName_usesGuest() {
        ReservationEmailEvent event = new ReservationEmailEvent();
        event.setGuestName(null);
        String body = EmailTemplateUtil.buildConfirmationBody(event);
        Assert.assertTrue(body.contains("Dear Guest"));
    }

    @Test
    public void testBuildConfirmationBody_blankFields_usesDash() {
        ReservationEmailEvent event = new ReservationEmailEvent();
        event.setGuestName("Jane");
        event.setReservationNo("");
        event.setRoomNumber("   ");
        String body = EmailTemplateUtil.buildConfirmationBody(event);
        Assert.assertTrue(body.contains("Reservation No: -"));
        Assert.assertTrue(body.contains("Room: -"));
    }

    @Test
    public void testBuildCancellationBody_includesDetails() {
        ReservationEmailEvent event = new ReservationEmailEvent();
        event.setGuestName("John Doe");
        event.setReservationNo("RES-002");
        event.setRoomNumber("201");

        String body = EmailTemplateUtil.buildCancellationBody(event);
        Assert.assertNotNull(body);
        Assert.assertTrue(body.contains("Dear John Doe"));
        Assert.assertTrue(body.contains("Your reservation has been cancelled"));
        Assert.assertTrue(body.contains("Reservation No: RES-002"));
        Assert.assertTrue(body.contains("Room: 201"));
        Assert.assertTrue(body.contains("If this was a mistake"));
        Assert.assertTrue(body.contains("OceanView Resort"));
    }

    @Test
    public void testBuildCancellationBody_nullGuestName_usesGuest() {
        ReservationEmailEvent event = new ReservationEmailEvent();
        event.setGuestName(null);
        String body = EmailTemplateUtil.buildCancellationBody(event);
        Assert.assertTrue(body.contains("Dear Guest"));
    }
}
