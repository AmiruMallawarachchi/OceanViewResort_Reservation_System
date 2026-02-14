package com.oceanview.resort.dto;

import java.math.BigDecimal;

/**
 * Lightweight DTO used by the admin dashboard to show key KPIs at a glance.
 */
public class DashboardSummaryDTO {
    private long totalRooms;
    private long availableRooms;
    private long occupiedRooms;
    private double occupancyRate;

    private long totalReservations;
    private long cancelledReservations;
    private double cancellationRate;

    private BigDecimal totalRevenue;
    private BigDecimal totalDiscounts;

    private long maintenanceRooms;
    private long todaysCheckins;
    private long pendingCheckins;
    private long activeStaff;
    private long staffOnLeave;
    private Double occupancyChangePercent;

    public long getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(long totalRooms) {
        this.totalRooms = totalRooms;
    }

    public long getAvailableRooms() {
        return availableRooms;
    }

    public void setAvailableRooms(long availableRooms) {
        this.availableRooms = availableRooms;
    }

    public long getOccupiedRooms() {
        return occupiedRooms;
    }

    public void setOccupiedRooms(long occupiedRooms) {
        this.occupiedRooms = occupiedRooms;
    }

    public double getOccupancyRate() {
        return occupancyRate;
    }

    public void setOccupancyRate(double occupancyRate) {
        this.occupancyRate = occupancyRate;
    }

    public long getTotalReservations() {
        return totalReservations;
    }

    public void setTotalReservations(long totalReservations) {
        this.totalReservations = totalReservations;
    }

    public long getCancelledReservations() {
        return cancelledReservations;
    }

    public void setCancelledReservations(long cancelledReservations) {
        this.cancelledReservations = cancelledReservations;
    }

    public double getCancellationRate() {
        return cancellationRate;
    }

    public void setCancellationRate(double cancellationRate) {
        this.cancellationRate = cancellationRate;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getTotalDiscounts() {
        return totalDiscounts;
    }

    public void setTotalDiscounts(BigDecimal totalDiscounts) {
        this.totalDiscounts = totalDiscounts;
    }

    public long getMaintenanceRooms() {
        return maintenanceRooms;
    }

    public void setMaintenanceRooms(long maintenanceRooms) {
        this.maintenanceRooms = maintenanceRooms;
    }

    public long getTodaysCheckins() {
        return todaysCheckins;
    }

    public void setTodaysCheckins(long todaysCheckins) {
        this.todaysCheckins = todaysCheckins;
    }

    public long getPendingCheckins() {
        return pendingCheckins;
    }

    public void setPendingCheckins(long pendingCheckins) {
        this.pendingCheckins = pendingCheckins;
    }

    public long getActiveStaff() {
        return activeStaff;
    }

    public void setActiveStaff(long activeStaff) {
        this.activeStaff = activeStaff;
    }

    public long getStaffOnLeave() {
        return staffOnLeave;
    }

    public void setStaffOnLeave(long staffOnLeave) {
        this.staffOnLeave = staffOnLeave;
    }

    public Double getOccupancyChangePercent() {
        return occupancyChangePercent;
    }

    public void setOccupancyChangePercent(Double occupancyChangePercent) {
        this.occupancyChangePercent = occupancyChangePercent;
    }
}

