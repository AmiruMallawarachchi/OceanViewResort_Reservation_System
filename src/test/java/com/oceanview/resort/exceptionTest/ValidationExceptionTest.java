package com.oceanview.resort.exceptionTest;

import com.oceanview.resort.exception.ValidationException;
import org.junit.Test;

import static org.junit.Assert.*;

public class ValidationExceptionTest {

    @Test
    public void testMessage() {
        ValidationException e = new ValidationException("Invalid email format");
        assertEquals("Invalid email format", e.getMessage());
    }

    @Test
    public void testIsRuntimeException() {
        assertTrue(new ValidationException("msg") instanceof RuntimeException);
    }
}
