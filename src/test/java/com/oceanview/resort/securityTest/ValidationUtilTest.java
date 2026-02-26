package com.oceanview.resort.securityTest;

import com.oceanview.resort.security.ValidationUtil;
import org.junit.Test;

/**
 * Tests for ValidationUtil (requireNonBlank, requirePositive, requirePositiveDecimal).
 */
public class ValidationUtilTest {

    @Test
    public void requireNonBlank_validDoesNotThrow() {
        ValidationUtil.requireNonBlank("ok", "msg");
    }

    @Test(expected = IllegalArgumentException.class)
    public void requireNonBlank_nullThrows() {
        ValidationUtil.requireNonBlank(null, "required");
    }

    @Test(expected = IllegalArgumentException.class)
    public void requireNonBlank_blankThrows() {
        ValidationUtil.requireNonBlank("   ", "required");
    }

    @Test
    public void requirePositive_validDoesNotThrow() {
        ValidationUtil.requirePositive(1L, "msg");
    }

    @Test(expected = IllegalArgumentException.class)
    public void requirePositive_zeroThrows() {
        ValidationUtil.requirePositive(0L, "must be positive");
    }

    @Test(expected = IllegalArgumentException.class)
    public void requirePositive_negativeThrows() {
        ValidationUtil.requirePositive(-1L, "must be positive");
    }

    @Test
    public void requirePositiveDecimal_validDoesNotThrow() {
        ValidationUtil.requirePositiveDecimal("1.5", "msg");
    }

    @Test(expected = IllegalArgumentException.class)
    public void requirePositiveDecimal_nullThrows() {
        ValidationUtil.requirePositiveDecimal(null, "required");
    }

    @Test(expected = IllegalArgumentException.class)
    public void requirePositiveDecimal_blankThrows() {
        ValidationUtil.requirePositiveDecimal("", "required");
    }

    @Test(expected = IllegalArgumentException.class)
    public void requirePositiveDecimal_zeroThrows() {
        ValidationUtil.requirePositiveDecimal("0", "must be positive");
    }

    @Test(expected = IllegalArgumentException.class)
    public void requirePositiveDecimal_negativeThrows() {
        ValidationUtil.requirePositiveDecimal("-1", "must be positive");
    }

    @Test(expected = IllegalArgumentException.class)
    public void requirePositiveDecimal_invalidNumberThrows() {
        ValidationUtil.requirePositiveDecimal("abc", "must be number");
    }
}
