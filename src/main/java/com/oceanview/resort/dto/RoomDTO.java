package com.oceanview.resort.dto;

public class RoomDTO {
    private long id;
    private String roomNumber;
    private long roomTypeId;
    private String roomTypeName;
    private String roomTypeRatePerNight;
    private int roomTypeMaxOccupancy;
    private String roomTypeAmenities;
    private int floor;
    private String status;
    private String description;
    private boolean fullAccess;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public long getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(long roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public String getRoomTypeName() {
        return roomTypeName;
    }

    public void setRoomTypeName(String roomTypeName) {
        this.roomTypeName = roomTypeName;
    }

    public String getRoomTypeRatePerNight() {
        return roomTypeRatePerNight;
    }

    public void setRoomTypeRatePerNight(String roomTypeRatePerNight) {
        this.roomTypeRatePerNight = roomTypeRatePerNight;
    }

    public int getRoomTypeMaxOccupancy() {
        return roomTypeMaxOccupancy;
    }

    public void setRoomTypeMaxOccupancy(int roomTypeMaxOccupancy) {
        this.roomTypeMaxOccupancy = roomTypeMaxOccupancy;
    }

    public String getRoomTypeAmenities() {
        return roomTypeAmenities;
    }

    public void setRoomTypeAmenities(String roomTypeAmenities) {
        this.roomTypeAmenities = roomTypeAmenities;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isFullAccess() {
        return fullAccess;
    }

    public void setFullAccess(boolean fullAccess) {
        this.fullAccess = fullAccess;
    }
}
