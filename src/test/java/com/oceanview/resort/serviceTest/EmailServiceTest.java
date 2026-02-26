package com.oceanview.resort.serviceTest;

import com.oceanview.resort.config.AppConfig;
import com.oceanview.resort.messaging.ReservationEmailEvent;
import com.oceanview.resort.messaging.ReservationEmailEventType;
import com.oceanview.resort.service.EmailService;
import com.oceanview.resort.util.EmailTemplateUtil;
import org.junit.Test;
import org.mockito.MockedStatic;

import javax.mail.Transport;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EmailServiceTest {

    @Test
    public void sendReservationEmail_nullEvent_noop() {
        new EmailService().sendReservationEmail(null);
        // no exception expected
    }

    @Test
    public void sendReservationEmail_emailDisabled_noop() {
        try (MockedStatic<AppConfig> appConfigMock = mockStatic(AppConfig.class)) {
            appConfigMock.when(() -> AppConfig.getBoolean("email.enabled", false)).thenReturn(false);

            ReservationEmailEvent event = new ReservationEmailEvent();
            event.setGuestEmail("guest@example.com");

            new EmailService().sendReservationEmail(event);

            appConfigMock.verify(() -> AppConfig.getBoolean("email.enabled", false), times(1));
        }
    }

    @Test
    public void sendReservationEmail_cancellation_usesCancellationTemplateAndSends() {
        try (MockedStatic<AppConfig> appConfigMock = mockStatic(AppConfig.class);
             MockedStatic<EmailTemplateUtil> templateMock = mockStatic(EmailTemplateUtil.class);
             MockedStatic<Transport> transportMock = mockStatic(Transport.class)) {

            appConfigMock.when(() -> AppConfig.getBoolean("email.enabled", false)).thenReturn(true);
            appConfigMock.when(() -> AppConfig.getProperty("smtp.host", "")).thenReturn("localhost");
            appConfigMock.when(() -> AppConfig.getProperty("smtp.port", "587")).thenReturn("587");
            appConfigMock.when(() -> AppConfig.getBoolean("smtp.auth", true)).thenReturn(false);
            appConfigMock.when(() -> AppConfig.getBoolean("smtp.starttls", true)).thenReturn(false);
            appConfigMock.when(() -> AppConfig.getBoolean("smtp.debug", false)).thenReturn(false);
            appConfigMock.when(() -> AppConfig.getProperty("smtp.username", "")).thenReturn("");
            appConfigMock.when(() -> AppConfig.getProperty("smtp.password", "")).thenReturn("");
            appConfigMock.when(() -> AppConfig.getProperty("smtp.from", "reservations@oceanviewresort.com"))
                    .thenReturn("reservations@oceanviewresort.com");
            appConfigMock.when(() -> AppConfig.getProperty("smtp.fromName", "OceanView Resort"))
                    .thenReturn("OceanView Resort");

            templateMock.when(() -> EmailTemplateUtil.buildCancellationSubject(any()))
                    .thenReturn("Cancellation subject");
            templateMock.when(() -> EmailTemplateUtil.buildCancellationBody(any()))
                    .thenReturn("Cancellation body");

            ReservationEmailEvent event = new ReservationEmailEvent();
            event.setGuestEmail("guest@example.com");
            event.setType(ReservationEmailEventType.CANCELLATION.name());

            new EmailService().sendReservationEmail(event);

            templateMock.verify(() -> EmailTemplateUtil.buildCancellationSubject(any()), times(1));
            templateMock.verify(() -> EmailTemplateUtil.buildCancellationBody(any()), times(1));
            transportMock.verify(() -> Transport.send(any()), atLeastOnce());
        }
    }

    @Test
    public void sendReservationEmail_missingGuestEmail_noop() {
        try (MockedStatic<AppConfig> appConfigMock = mockStatic(AppConfig.class);
             MockedStatic<Transport> transportMock = mockStatic(Transport.class);
             MockedStatic<EmailTemplateUtil> templateMock = mockStatic(EmailTemplateUtil.class)) {

            appConfigMock.when(() -> AppConfig.getBoolean("email.enabled", false)).thenReturn(true);

            ReservationEmailEvent event = new ReservationEmailEvent();
            event.setGuestEmail("   "); // blank email

            new EmailService().sendReservationEmail(event);

            // No templates or transport should be invoked when email is missing
            templateMock.verifyNoInteractions();
            transportMock.verifyNoInteractions();
        }
    }

    @Test
    public void sendReservationEmail_confirmation_usesConfirmationTemplateAndHandlesException() {
        try (MockedStatic<AppConfig> appConfigMock = mockStatic(AppConfig.class);
             MockedStatic<EmailTemplateUtil> templateMock = mockStatic(EmailTemplateUtil.class);
             MockedStatic<Transport> transportMock = mockStatic(Transport.class)) {

            appConfigMock.when(() -> AppConfig.getBoolean("email.enabled", false)).thenReturn(true);
            appConfigMock.when(() -> AppConfig.getProperty("smtp.host", "")).thenReturn("localhost");
            appConfigMock.when(() -> AppConfig.getProperty("smtp.port", "587")).thenReturn("587");
            appConfigMock.when(() -> AppConfig.getBoolean("smtp.auth", true)).thenReturn(true);
            appConfigMock.when(() -> AppConfig.getBoolean("smtp.starttls", true)).thenReturn(false);
            appConfigMock.when(() -> AppConfig.getBoolean("smtp.debug", false)).thenReturn(false);
            appConfigMock.when(() -> AppConfig.getProperty("smtp.username", "")).thenReturn("smtp-user");
            appConfigMock.when(() -> AppConfig.getProperty("smtp.password", "")).thenReturn("smtp-pass");
            appConfigMock.when(() -> AppConfig.getProperty("smtp.from", "reservations@oceanviewresort.com"))
                    .thenReturn("reservations@oceanviewresort.com");
            appConfigMock.when(() -> AppConfig.getProperty("smtp.fromName", "OceanView Resort"))
                    .thenReturn("OceanView Resort");

            templateMock.when(() -> EmailTemplateUtil.buildConfirmationSubject(any()))
                    .thenReturn("Confirmation subject");
            templateMock.when(() -> EmailTemplateUtil.buildConfirmationBody(any()))
                    .thenReturn("Confirmation body");

            // Cause Transport.send to throw so the catch block is exercised
            transportMock.when(() -> Transport.send(any())).thenThrow(new RuntimeException("test failure"));

            ReservationEmailEvent event = new ReservationEmailEvent();
            event.setGuestEmail("guest@example.com");
            event.setType(ReservationEmailEventType.CONFIRMATION.name());

            new EmailService().sendReservationEmail(event);

            templateMock.verify(() -> EmailTemplateUtil.buildConfirmationSubject(any()), times(1));
            templateMock.verify(() -> EmailTemplateUtil.buildConfirmationBody(any()), times(1));
            transportMock.verify(() -> Transport.send(any()), atLeastOnce());
        }
    }

    @Test
    public void sendPasswordResetOtp_argumentValidation_noopForBlankValues() {
        EmailService service = new EmailService();
        service.sendPasswordResetOtp(null, "123456");
        service.sendPasswordResetOtp("   ", "123456");
        service.sendPasswordResetOtp("user@example.com", null);
        service.sendPasswordResetOtp("user@example.com", "   ");
    }

    @Test
    public void sendPasswordResetOtp_emailDisabled_noop() {
        try (MockedStatic<AppConfig> appConfigMock = mockStatic(AppConfig.class)) {
            appConfigMock.when(() -> AppConfig.getBoolean("email.enabled", false)).thenReturn(false);

            EmailService service = new EmailService();
            service.sendPasswordResetOtp("user@example.com", "123456");

            // AppConfig should be consulted once for the feature flag
            appConfigMock.verify(() -> AppConfig.getBoolean("email.enabled", false), times(1));
        }
    }

    @Test
    public void sendPasswordResetOtp_emailEnabled_sends() {
        try (MockedStatic<AppConfig> appConfigMock = mockStatic(AppConfig.class);
             MockedStatic<Transport> transportMock = mockStatic(Transport.class)) {

            appConfigMock.when(() -> AppConfig.getBoolean("email.enabled", false)).thenReturn(true);
            appConfigMock.when(() -> AppConfig.getProperty("smtp.host", "")).thenReturn("localhost");
            appConfigMock.when(() -> AppConfig.getProperty("smtp.port", "587")).thenReturn("587");
            appConfigMock.when(() -> AppConfig.getBoolean("smtp.auth", true)).thenReturn(true);
            appConfigMock.when(() -> AppConfig.getBoolean("smtp.starttls", true)).thenReturn(false);
            appConfigMock.when(() -> AppConfig.getBoolean("smtp.debug", false)).thenReturn(false);
            appConfigMock.when(() -> AppConfig.getProperty("smtp.username", "")).thenReturn("smtp-user");
            appConfigMock.when(() -> AppConfig.getProperty("smtp.password", "")).thenReturn("smtp-pass");
            appConfigMock.when(() -> AppConfig.getProperty("smtp.from", "reservations@oceanviewresort.com"))
                    .thenReturn("reservations@oceanviewresort.com");
            // Force buildFromAddress branch where fromName is blank
            appConfigMock.when(() -> AppConfig.getProperty("smtp.fromName", "OceanView Resort"))
                    .thenReturn("");

            EmailService service = new EmailService();
            service.sendPasswordResetOtp("user@example.com", "123456");

            transportMock.verify(() -> Transport.send(any()), atLeastOnce());
        }
    }
}

