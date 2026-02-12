package com.oceanview.resort.security;

public final class ValidationUtil {
    private ValidationUtil() {
    }

    public static void requireNonBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requirePositive(long value, String message) {
        if (value <= 0) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requirePositiveDecimal(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        try {
            if (Double.parseDouble(value) <= 0) {
                throw new IllegalArgumentException(message);
            }
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(message);
        }
    }
}
