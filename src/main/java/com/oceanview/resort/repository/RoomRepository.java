package com.oceanview.resort.repository;

import com.oceanview.resort.model.Room;

import java.time.LocalDate;
import java.util.List;

public interface RoomRepository {
    Room create(Room room);
    Room update(Room room);
    boolean delete(long id);
    Room findById(long id);
    Room findByRoomNumber(String roomNumber);
    List<Room> findAll();
    List<Room> findAvailable(LocalDate checkIn, LocalDate checkOut);
    /**
     * Optional optimization that uses the database stored procedure
     * {@code get_available_room_ids} to determine availability. Callers
     * should typically guard usage behind a configuration flag.
     */
    List<Room> findAvailableUsingProcedure(LocalDate checkIn, LocalDate checkOut);
}
