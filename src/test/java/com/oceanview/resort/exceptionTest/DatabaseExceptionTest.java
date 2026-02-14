package com.oceanview.resort.exceptionTest;

import com.oceanview.resort.exception.DatabaseException;
import org.junit.Test;

import static org.junit.Assert.*;

public class DatabaseExceptionTest {

    @Test
    public void testMessage() {
        DatabaseException e = new DatabaseException("Connection failed");
        assertEquals("Connection failed", e.getMessage());
        assertNull(e.getCause());
    }

    @Test
    public void testMessageAndCause() {
        Throwable cause = new RuntimeException("root cause");
        DatabaseException e = new DatabaseException("DB error", cause);
        assertEquals("DB error", e.getMessage());
        assertSame(cause, e.getCause());
    }

    @Test
    public void testIsRuntimeException() {
        assertTrue(new DatabaseException("msg") instanceof RuntimeException);
    }
}
