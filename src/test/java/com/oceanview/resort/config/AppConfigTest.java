package com.oceanview.resort.config;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Properties;

public class AppConfigTest {

    private Properties getInternalProperties() throws Exception {
        Field field = AppConfig.class.getDeclaredField("PROPERTIES");
        field.setAccessible(true);
        return (Properties) field.get(null);
    }

    @Test
    public void testGetPropertyReturnsTrimmedValue() throws Exception {
        Properties properties = getInternalProperties();
        properties.setProperty("test.trim.key", "  value-with-spaces  ");

        String result = AppConfig.getProperty("test.trim.key");

        Assert.assertEquals("value-with-spaces", result);
    }

    @Test
    public void testGetPropertyReturnsDefaultWhenMissingOrBlank() throws Exception {
        Properties properties = getInternalProperties();
        properties.remove("test.missing.key");
        properties.setProperty("test.blank.key", "   ");

        String defaultValue = "default";

        Assert.assertEquals(defaultValue, AppConfig.getProperty("test.missing.key", defaultValue));
        Assert.assertEquals(defaultValue, AppConfig.getProperty("test.blank.key", defaultValue));
    }

    @Test
    public void testGetBooleanParsesTrueAndOne() throws Exception {
        Properties properties = getInternalProperties();
        properties.setProperty("bool.true", "true");
        properties.setProperty("bool.TRUE", "TRUE");
        properties.setProperty("bool.one", "1");

        Assert.assertTrue(AppConfig.getBoolean("bool.true", false));
        Assert.assertTrue(AppConfig.getBoolean("bool.TRUE", false));
        Assert.assertTrue(AppConfig.getBoolean("bool.one", false));
    }

    @Test
    public void testGetBooleanFallsBackToDefault() throws Exception {
        Properties properties = getInternalProperties();
        properties.remove("bool.missing");

        Assert.assertTrue(AppConfig.getBoolean("bool.missing", true));
        Assert.assertFalse(AppConfig.getBoolean("bool.missing", false));
    }

    @Test
    public void testGetBooleanReturnsFalseForInvalidValues() throws Exception {
        Properties properties = getInternalProperties();
        properties.setProperty("bool.invalid", "not-a-boolean");

        // For an explicitly set non-boolean value, current implementation ignores the default
        // and simply returns false because it only treats "true" or "1" as true.
        Assert.assertFalse(AppConfig.getBoolean("bool.invalid", true));
        Assert.assertFalse(AppConfig.getBoolean("bool.invalid", false));
    }

    @Test
    public void testGetIntParsesValidIntegerOrReturnsDefault() throws Exception {
        Properties properties = getInternalProperties();
        properties.setProperty("int.valid", "42");
        properties.remove("int.missing");
        properties.setProperty("int.invalid", "abc");

        Assert.assertEquals(42, AppConfig.getInt("int.valid", 0));
        Assert.assertEquals(5, AppConfig.getInt("int.missing", 5));
        Assert.assertEquals(7, AppConfig.getInt("int.invalid", 7));
    }
}

