package com.oceanview.resort.strategy;

import java.math.BigDecimal;
import java.util.List;

/**
 * Context object that holds additional information needed for discount calculation.
 * This is passed to discount calculation strategies to provide them with
 * necessary context (selected promotions, manual discounts, etc.).
 */
public class DiscountCalculationContext {
    private List<Long> selectedPromotionIds;
    private BigDecimal manualDiscountPercent;
    
    public DiscountCalculationContext() {
    }
    
    public DiscountCalculationContext(List<Long> selectedPromotionIds, BigDecimal manualDiscountPercent) {
        this.selectedPromotionIds = selectedPromotionIds;
        this.manualDiscountPercent = manualDiscountPercent;
    }
    
    public List<Long> getSelectedPromotionIds() {
        return selectedPromotionIds;
    }
    
    public void setSelectedPromotionIds(List<Long> selectedPromotionIds) {
        this.selectedPromotionIds = selectedPromotionIds;
    }
    
    public BigDecimal getManualDiscountPercent() {
        return manualDiscountPercent;
    }
    
    public void setManualDiscountPercent(BigDecimal manualDiscountPercent) {
        this.manualDiscountPercent = manualDiscountPercent;
    }
}
