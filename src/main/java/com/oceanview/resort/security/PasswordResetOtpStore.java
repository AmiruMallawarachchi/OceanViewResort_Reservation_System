package com.oceanview.resort.security;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory store for password-reset OTPs. Each OTP expires after a fixed duration.
 * For production with multiple instances, consider Redis or DB-backed store.
 */
public final class PasswordResetOtpStore {

    private static final int OTP_EXPIRY_SECONDS = 600; // 10 minutes
    private static final int OTP_LENGTH = 6;

    private final Map<String, OtpEntry> store = new ConcurrentHashMap<>();

    public String generateAndStore(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        String normalized = email.trim().toLowerCase();
        String otp = generateOtp();
        store.put(normalized, new OtpEntry(otp, Instant.now().plusSeconds(OTP_EXPIRY_SECONDS)));
        return otp;
    }

    public boolean verifyAndConsume(String email, String otp) {
        if (email == null || email.isBlank() || otp == null || otp.isBlank()) {
            return false;
        }
        String normalized = email.trim().toLowerCase();
        OtpEntry entry = store.get(normalized);
        if (entry == null || entry.expiresAt.isBefore(Instant.now())) {
            store.remove(normalized);
            return false;
        }
        if (!entry.otp.equals(otp.trim())) {
            return false;
        }
        store.remove(normalized);
        return true;
    }

    private static String generateOtp() {
        StringBuilder sb = new StringBuilder(OTP_LENGTH);
        java.util.Random r = new java.util.Random();
        for (int i = 0; i < OTP_LENGTH; i++) {
            sb.append(r.nextInt(10));
        }
        return sb.toString();
    }

    private static final class OtpEntry {
        final String otp;
        final Instant expiresAt;

        OtpEntry(String otp, Instant expiresAt) {
            this.otp = otp;
            this.expiresAt = expiresAt;
        }
    }
}
