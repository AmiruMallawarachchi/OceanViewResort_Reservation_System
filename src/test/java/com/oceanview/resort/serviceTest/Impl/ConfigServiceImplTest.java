package com.oceanview.resort.serviceTest.Impl;

import com.oceanview.resort.dao.SystemConfigDAO;
import com.oceanview.resort.service.impl.ConfigServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConfigServiceImplTest {

    @Mock
    private SystemConfigDAO systemConfigDAO;

    @Test
    public void getTaxRateDecimal_nullOrBlank_usesDefault() {
        ConfigServiceImpl service = new ConfigServiceImpl(systemConfigDAO);

        when(systemConfigDAO.get("tax_rate_percent")).thenReturn(null);
        assertEquals(0.10, service.getTaxRateDecimal(), 0.00001);

        when(systemConfigDAO.get("tax_rate_percent")).thenReturn("   ");
        assertEquals(0.10, service.getTaxRateDecimal(), 0.00001);
    }

    @Test
    public void getTaxRateDecimal_parsesAndClamps() {
        ConfigServiceImpl service = new ConfigServiceImpl(systemConfigDAO);

        when(systemConfigDAO.get("tax_rate_percent")).thenReturn("15.5");
        assertEquals(0.155, service.getTaxRateDecimal(), 0.00001);

        when(systemConfigDAO.get("tax_rate_percent")).thenReturn("150");
        assertEquals(1.0, service.getTaxRateDecimal(), 0.00001);

        when(systemConfigDAO.get("tax_rate_percent")).thenReturn("-5");
        assertEquals(0.0, service.getTaxRateDecimal(), 0.00001);
    }

    @Test
    public void getTaxRateDecimal_invalidNumber_usesDefault() {
        ConfigServiceImpl service = new ConfigServiceImpl(systemConfigDAO);

        when(systemConfigDAO.get("tax_rate_percent")).thenReturn("not_a_number");
        assertEquals(0.10, service.getTaxRateDecimal(), 0.00001);
    }

    @Test
    public void setTaxRatePercent_clampsAndDelegatesToDao() {
        ConfigServiceImpl service = new ConfigServiceImpl(systemConfigDAO);

        service.setTaxRatePercent(15.5);
        verify(systemConfigDAO).set("tax_rate_percent", "15.5");

        service.setTaxRatePercent(150);
        verify(systemConfigDAO).set("tax_rate_percent", "100.0");

        service.setTaxRatePercent(-5);
        verify(systemConfigDAO).set("tax_rate_percent", "0.0");
    }
}

