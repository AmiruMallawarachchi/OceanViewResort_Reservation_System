package com.oceanview.resort.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Bill {
    private long id;
    private String billNo;
    private Reservation reservation;
    private int numberOfNights;
    private BigDecimal roomRate;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal netAmount;
    private User generatedBy;
    private LocalDateTime generatedAt;

    public Bill() {
    }

    public Bill(long id, String billNo, Reservation reservation, int numberOfNights, BigDecimal roomRate,
                BigDecimal totalAmount, BigDecimal discountAmount, BigDecimal taxAmount, BigDecimal netAmount,
                User generatedBy, LocalDateTime generatedAt) {
        this.id = id;
        this.billNo = billNo;
        this.reservation = reservation;
        this.numberOfNights = numberOfNights;
        this.roomRate = roomRate;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.taxAmount = taxAmount;
        this.netAmount = netAmount;
        this.generatedBy = generatedBy;
        this.generatedAt = generatedAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public int getNumberOfNights() {
        return numberOfNights;
    }

    public void setNumberOfNights(int numberOfNights) {
        this.numberOfNights = numberOfNights;
    }

    public BigDecimal getRoomRate() {
        return roomRate;
    }

    public void setRoomRate(BigDecimal roomRate) {
        this.roomRate = roomRate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public User getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(User generatedBy) {
        this.generatedBy = generatedBy;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
}
