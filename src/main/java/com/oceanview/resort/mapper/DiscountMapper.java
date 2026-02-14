package com.oceanview.resort.mapper;

import com.oceanview.resort.dto.DiscountDTO;
import com.oceanview.resort.model.Discount;
import com.oceanview.resort.model.enums.DiscountType;
import com.oceanview.resort.model.enums.GuestType;

import java.math.BigDecimal;

public final class DiscountMapper {
    private DiscountMapper() {
    }

    public static DiscountDTO toDTO(Discount discount) {
        if (discount == null) {
            return null;
        }
        DiscountDTO dto = new DiscountDTO();
        dto.setId(discount.getId());
        dto.setName(discount.getName());
        dto.setDiscountType(discount.getDiscountType() == null ? null : discount.getDiscountType().name());
        dto.setGuestType(discount.getGuestType() == null ? null : discount.getGuestType().name());
        dto.setPercent(discount.getPercent() == null ? null : discount.getPercent().toPlainString());
        dto.setDescription(discount.getDescription());
        dto.setActive(discount.isActive());
        return dto;
    }

    public static Discount toEntity(DiscountDTO dto) {
        if (dto == null) {
            return null;
        }
        Discount discount = new Discount();
        discount.setId(dto.getId());
        discount.setName(dto.getName());
        if (dto.getDiscountType() != null) {
            discount.setDiscountType(DiscountType.valueOf(dto.getDiscountType()));
        }
        if (dto.getGuestType() != null && !dto.getGuestType().isBlank()) {
            discount.setGuestType(GuestType.valueOf(dto.getGuestType()));
        }
        if (dto.getPercent() != null && !dto.getPercent().isBlank()) {
            discount.setPercent(new BigDecimal(dto.getPercent()));
        }
        discount.setDescription(dto.getDescription());
        discount.setActive(dto.isActive());
        return discount;
    }
}
