package com.oceanview.resort.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateUtil {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private DateUtil() {
    }

    public static LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDate.parse(value, DATE_FORMAT);
    }

    public static String formatDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return DATE_FORMAT.format(date);
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return DATETIME_FORMAT.format(dateTime);
    }
}
