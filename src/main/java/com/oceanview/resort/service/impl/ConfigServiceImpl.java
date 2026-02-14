package com.oceanview.resort.service.impl;

import com.oceanview.resort.dao.SystemConfigDAO;
import com.oceanview.resort.service.ConfigService;

public class ConfigServiceImpl implements ConfigService {
    private static final String TAX_RATE_KEY = "tax_rate_percent";
    private static final double DEFAULT_TAX_PERCENT = 10.0;

    private final SystemConfigDAO systemConfigDAO;

    public ConfigServiceImpl(SystemConfigDAO systemConfigDAO) {
        this.systemConfigDAO = systemConfigDAO;
    }

    @Override
    public double getTaxRateDecimal() {
        String value = systemConfigDAO.get(TAX_RATE_KEY);
        if (value == null || value.isBlank()) {
            return DEFAULT_TAX_PERCENT / 100.0;
        }
        try {
            double percent = Double.parseDouble(value.trim());
            return Math.max(0, Math.min(100, percent)) / 100.0;
        } catch (NumberFormatException e) {
            return DEFAULT_TAX_PERCENT / 100.0;
        }
    }

    @Override
    public void setTaxRatePercent(double percent) {
        double clamped = Math.max(0, Math.min(100, percent));
        systemConfigDAO.set(TAX_RATE_KEY, String.valueOf(clamped));
    }
}
