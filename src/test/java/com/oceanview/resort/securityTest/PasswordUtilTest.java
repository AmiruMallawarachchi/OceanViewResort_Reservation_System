package com.oceanview.resort.securityTest;

import com.oceanview.resort.security.PasswordUtil;
import org.junit.Test;

import static org.junit.Assert.*;

public class PasswordUtilTest {

    @Test
    public void hashPassword_sameInputProducesSameHash() {
        String hash1 = PasswordUtil.hashPassword("secret123");
        String hash2 = PasswordUtil.hashPassword("secret123");

        assertNotNull(hash1);
        assertEquals(hash1, hash2);
    }

    @Test
    public void hashPassword_differentInputProducesDifferentHash() {
        String hash1 = PasswordUtil.hashPassword("secret123");
        String hash2 = PasswordUtil.hashPassword("other456");

        assertNotEquals(hash1, hash2);
    }

    @Test
    public void verifyPassword_matchesAndMismatches() {
        String raw = "myPassword";
        String hash = PasswordUtil.hashPassword(raw);

        assertTrue(PasswordUtil.verifyPassword(raw, hash));
        assertFalse(PasswordUtil.verifyPassword("wrong", hash));
        assertFalse(PasswordUtil.verifyPassword(null, hash));
        assertFalse(PasswordUtil.verifyPassword(raw, null));
    }
}

