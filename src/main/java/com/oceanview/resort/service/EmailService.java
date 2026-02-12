package com.oceanview.resort.service;

import com.oceanview.resort.config.AppConfig;
import com.oceanview.resort.messaging.ReservationEmailEvent;
import com.oceanview.resort.messaging.ReservationEmailEventType;
import com.oceanview.resort.util.EmailTemplateUtil;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class EmailService {
    public void sendReservationEmail(ReservationEmailEvent event) {
        if (event == null) {
            return;
        }
        if (!AppConfig.getBoolean("email.enabled", false)) {
            return;
        }
        if (event.getGuestEmail() == null || event.getGuestEmail().isBlank()) {
            System.err.println("Skipping email, guest email is missing for reservation " + event.getReservationNo());
            return;
        }

        String type = event.getType();
        String subject;
        String body;
        if (ReservationEmailEventType.CANCELLATION.name().equalsIgnoreCase(type)) {
            subject = EmailTemplateUtil.buildCancellationSubject(event);
            body = EmailTemplateUtil.buildCancellationBody(event);
        } else {
            subject = EmailTemplateUtil.buildConfirmationSubject(event);
            body = EmailTemplateUtil.buildConfirmationBody(event);
        }

        try {
            Session session = createSession();
            MimeMessage message = new MimeMessage(session);
            message.setFrom(buildFromAddress());
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(event.getGuestEmail(), false));
            message.setSubject(subject, StandardCharsets.UTF_8.name());
            message.setText(body, StandardCharsets.UTF_8.name());
            Transport.send(message);
        } catch (Exception ex) {
            System.err.println("Failed to send reservation email: " + ex.getMessage());
        }
    }

    private Session createSession() {
        Properties props = new Properties();
        String host = AppConfig.getProperty("smtp.host", "");
        String port = AppConfig.getProperty("smtp.port", "587");
        boolean auth = AppConfig.getBoolean("smtp.auth", true);
        boolean startTls = AppConfig.getBoolean("smtp.starttls", true);
        boolean debug = AppConfig.getBoolean("smtp.debug", false);

        props.put("mail.smtp.auth", String.valueOf(auth));
        props.put("mail.smtp.starttls.enable", String.valueOf(startTls));
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.ssl.trust", host);
        props.put("mail.debug", String.valueOf(debug));

        String username = AppConfig.getProperty("smtp.username", "");
        String password = AppConfig.getProperty("smtp.password", "");
        if (auth && !username.isBlank()) {
            return Session.getInstance(props, new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
        }
        return Session.getInstance(props);
    }

    /**
     * Sends a password-reset OTP to the given email address.
     * No-op if email is disabled or address is blank.
     */
    public void sendPasswordResetOtp(String toEmail, String otp) {
        if (toEmail == null || toEmail.isBlank() || otp == null || otp.isBlank()) {
            return;
        }
        if (!AppConfig.getBoolean("email.enabled", false)) {
            return;
        }
        String subject = "OceanView Resort – Password reset code";
        String body = "Your password reset code is: " + otp + "\n\n"
                + "This code expires in 10 minutes. If you did not request this, please ignore this email.\n\n"
                + "— OceanView Resort";
        try {
            Session session = createSession();
            MimeMessage message = new MimeMessage(session);
            message.setFrom(buildFromAddress());
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail.trim(), false));
            message.setSubject(subject, StandardCharsets.UTF_8.name());
            message.setText(body, StandardCharsets.UTF_8.name());
            Transport.send(message);
        } catch (Exception ex) {
            System.err.println("Failed to send password reset OTP email: " + ex.getMessage());
        }
    }

    private InternetAddress buildFromAddress() throws Exception {
        String from = AppConfig.getProperty("smtp.from", "reservations@oceanviewresort.com");
        String fromName = AppConfig.getProperty("smtp.fromName", "OceanView Resort");
        if (fromName == null || fromName.isBlank()) {
            return new InternetAddress(from);
        }
        return new InternetAddress(from, fromName, StandardCharsets.UTF_8.name());
    }
}
