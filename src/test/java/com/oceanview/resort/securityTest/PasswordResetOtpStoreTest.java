package com.oceanview.resort.securityTest;

import com.oceanview.resort.security.PasswordResetOtpStore;
import org.junit.Test;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Map;

import static org.junit.Assert.*;

public class PasswordResetOtpStoreTest {

    @Test
    public void generateAndStore_nullOrBlankEmail_returnsNull() {
        PasswordResetOtpStore store = new PasswordResetOtpStore();
        assertNull(store.generateAndStore(null));
        assertNull(store.generateAndStore("   "));
    }

    @Test
    public void generateAndStore_andVerify_successThenConsumed() {
        PasswordResetOtpStore store = new PasswordResetOtpStore();
        String email = "user@example.com";

        String otp = store.generateAndStore(email);
        assertNotNull(otp);

        assertTrue(store.verifyAndConsume(email, otp));
        assertFalse("OTP should be consumed after successful verification",
                store.verifyAndConsume(email, otp));
    }

    @Test
    public void verifyAndConsume_wrongOrExpiredOtp_returnsFalse() throws Exception {
        PasswordResetOtpStore store = new PasswordResetOtpStore();
        String email = "user2@example.com";
        String otp = store.generateAndStore(email);

        assertFalse(store.verifyAndConsume(email, "wrong"));

        // Force the OTP to be expired using reflection
        Field storeField = PasswordResetOtpStore.class.getDeclaredField("store");
        storeField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, ?> internalStore = (Map<String, ?>) storeField.get(store);

        Object entry = internalStore.get(email.toLowerCase());
        assertNotNull(entry);

        Field expiresAtField = entry.getClass().getDeclaredField("expiresAt");
        expiresAtField.setAccessible(true);
        expiresAtField.set(entry, Instant.now().minusSeconds(1));

        assertFalse("Expired OTP should not be accepted",
                store.verifyAndConsume(email, otp));
    }
}

