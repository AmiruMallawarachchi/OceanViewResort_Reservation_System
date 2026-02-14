package com.oceanview.resort.exceptionTest;

import com.oceanview.resort.exception.BusinessException;
import org.junit.Test;

import static org.junit.Assert.*;

public class BusinessExceptionTest {

    @Test
    public void testMessage() {
        BusinessException e = new BusinessException("Invalid operation");
        assertEquals("Invalid operation", e.getMessage());
    }

    @Test
    public void testIsRuntimeException() {
        assertTrue(new BusinessException("msg") instanceof RuntimeException);
    }
}
