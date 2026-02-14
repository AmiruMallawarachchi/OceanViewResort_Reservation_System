package com.oceanview.resort.service;

public interface ConfigService {
    /**
     * Returns tax rate as a decimal (e.g. 0.10 for 10%). Default 0.10 if not set.
     */
    double getTaxRateDecimal();

    /**
     * Sets tax rate from percent (e.g. 10 for 10%).
     */
    void setTaxRatePercent(double percent);
}
