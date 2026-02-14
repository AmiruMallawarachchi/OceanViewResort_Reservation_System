package com.oceanview.resort.mapper;

import com.oceanview.resort.dto.BillDTO;
import com.oceanview.resort.model.Bill;
import com.oceanview.resort.util.DateUtil;

public final class BillMapper {
    private BillMapper() {
    }

    public static BillDTO toDTO(Bill bill) {
        if (bill == null) {
            return null;
        }
        BillDTO dto = new BillDTO();
        dto.setId(bill.getId());
        dto.setBillNo(bill.getBillNo());
        if (bill.getReservation() != null) {
            dto.setReservationNo(bill.getReservation().getReservationNo());
            if (bill.getReservation().getGuest() != null) {
                dto.setGuestName(bill.getReservation().getGuest().getFullName());
            }
            if (bill.getReservation().getRoom() != null) {
                dto.setRoomNumber(bill.getReservation().getRoom().getRoomNumber());
            }
        }
        dto.setNumberOfNights(bill.getNumberOfNights());
        dto.setRoomRate(bill.getRoomRate() == null ? null : bill.getRoomRate().toPlainString());
        dto.setTotalAmount(bill.getTotalAmount() == null ? null : bill.getTotalAmount().toPlainString());
        dto.setDiscountAmount(bill.getDiscountAmount() == null ? null : bill.getDiscountAmount().toPlainString());
        dto.setTaxAmount(bill.getTaxAmount() == null ? null : bill.getTaxAmount().toPlainString());
        dto.setNetAmount(bill.getNetAmount() == null ? null : bill.getNetAmount().toPlainString());
        dto.setGeneratedAt(DateUtil.formatDateTime(bill.getGeneratedAt()));
        return dto;
    }
}
