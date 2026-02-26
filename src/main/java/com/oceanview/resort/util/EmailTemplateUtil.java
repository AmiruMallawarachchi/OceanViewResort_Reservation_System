package com.oceanview.resort.util;

import com.oceanview.resort.messaging.ReservationEmailEvent;

public final class EmailTemplateUtil {
    private EmailTemplateUtil() {
    }

    public static String buildConfirmationSubject(ReservationEmailEvent event) {
        return "OceanView Resort - Reservation Confirmation";
    }

    public static String buildCancellationSubject(ReservationEmailEvent event) {
        return "OceanView Resort - Reservation Cancelled";
    }

    public static String buildConfirmationBody(ReservationEmailEvent event) {
        StringBuilder body = new StringBuilder();
        body.append("Dear ").append(blankFallback(event.getGuestName(), "Guest")).append(",\n\n");
        body.append("Thank you for making a reservation with OceanView Resort.\n");
        body.append("Here are your reservation details:\n\n");
        appendReservationDetails(body, event);
        body.append("\nWe look forward to welcoming you!\n\n");
        body.append("OceanView Resort");
        return body.toString();
    }

    public static String buildCancellationBody(ReservationEmailEvent event) {
        StringBuilder body = new StringBuilder();
        body.append("Dear ").append(blankFallback(event.getGuestName(), "Guest")).append(",\n\n");
        body.append("Your reservation has been cancelled.\n");
        body.append("Reservation details:\n\n");
        appendReservationDetails(body, event);
        body.append("\nIf this was a mistake, please contact our front desk.\n\n");
        body.append("OceanView Resort");
        return body.toString();
    }

    private static void appendReservationDetails(StringBuilder body, ReservationEmailEvent event) {
        body.append("Reservation No: ").append(blankFallback(event.getReservationNo(), "-")).append("\n");
        body.append("Room: ").append(blankFallback(event.getRoomNumber(), "-")).append("\n");
        body.append("Room Type: ").append(blankFallback(event.getRoomTypeName(), "-")).append("\n");
        body.append("Check-in: ").append(blankFallback(event.getCheckInDate(), "-")).append("\n");
        body.append("Check-out: ").append(blankFallback(event.getCheckOutDate(), "-")).append("\n");
        body.append("Status: ").append(blankFallback(event.getStatus(), "-")).append("\n");
    }

    private static String blankFallback(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
