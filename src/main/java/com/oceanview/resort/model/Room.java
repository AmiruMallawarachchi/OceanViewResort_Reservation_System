package com.oceanview.resort.model;

import com.oceanview.resort.model.enums.RoomStatus;

public class Room {
    private long id;
    private String roomNumber;
    private RoomType roomType;
    private int floor;
    private RoomStatus status;
    private String description;
    private boolean fullAccess;

    public Room() {
    }

    public Room(long id, String roomNumber, RoomType roomType, int floor, RoomStatus status, String description) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.floor = floor;
        this.status = status;
        this.description = description;
    }

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

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
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
