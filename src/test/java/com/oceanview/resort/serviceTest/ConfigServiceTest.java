package com.oceanview.resort.serviceTest;

import com.oceanview.resort.dao.SystemConfigDAO;
import com.oceanview.resort.service.ConfigService;
import com.oceanview.resort.service.impl.ConfigServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for ConfigService interface contract (via ConfigServiceImpl).
 */
@RunWith(MockitoJUnitRunner.class)
public class ConfigServiceTest {

    @Mock
    private SystemConfigDAO systemConfigDAO;

    private ConfigService configService;

    @Test
    public void getTaxRateDecimal_nullOrBlank_returnsDefault() {
        configService = new ConfigServiceImpl(systemConfigDAO);

        when(systemConfigDAO.get("tax_rate_percent")).thenReturn(null);
        assertEquals(0.10, configService.getTaxRateDecimal(), 0.00001);

        when(systemConfigDAO.get("tax_rate_percent")).thenReturn("   ");
        assertEquals(0.10, configService.getTaxRateDecimal(), 0.00001);
    }

    @Test
    public void getTaxRateDecimal_validValue_returnsParsedAndClamped() {
        configService = new ConfigServiceImpl(systemConfigDAO);

        when(systemConfigDAO.get("tax_rate_percent")).thenReturn("15.5");
        assertEquals(0.155, configService.getTaxRateDecimal(), 0.00001);

        when(systemConfigDAO.get("tax_rate_percent")).thenReturn("150");
        assertEquals(1.0, configService.getTaxRateDecimal(), 0.00001);

        when(systemConfigDAO.get("tax_rate_percent")).thenReturn("-5");
        assertEquals(0.0, configService.getTaxRateDecimal(), 0.00001);
    }

    @Test
    public void getTaxRateDecimal_invalidNumber_returnsDefault() {
        configService = new ConfigServiceImpl(systemConfigDAO);

        when(systemConfigDAO.get("tax_rate_percent")).thenReturn("invalid");
        assertEquals(0.10, configService.getTaxRateDecimal(), 0.00001);
    }

    @Test
    public void setTaxRatePercent_clampsAndPersists() {
        configService = new ConfigServiceImpl(systemConfigDAO);

        configService.setTaxRatePercent(15.5);
        verify(systemConfigDAO).set("tax_rate_percent", "15.5");

        configService.setTaxRatePercent(150);
        verify(systemConfigDAO).set("tax_rate_percent", "100.0");

        configService.setTaxRatePercent(-5);
        verify(systemConfigDAO).set("tax_rate_percent", "0.0");
    }
}
