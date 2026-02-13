package com.oceanview.resort.repository;

import com.oceanview.resort.model.RoomType;

import java.util.List;

public interface RoomTypeRepository {
    RoomType create(RoomType roomType);
    RoomType update(RoomType roomType);
    boolean delete(long id);
    RoomType findById(long id);
    List<RoomType> findAll();
}
