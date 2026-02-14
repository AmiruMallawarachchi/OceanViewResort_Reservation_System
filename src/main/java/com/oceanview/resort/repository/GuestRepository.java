package com.oceanview.resort.repository;

import com.oceanview.resort.model.Guest;

import java.util.List;

public interface GuestRepository {
    Guest create(Guest guest);
    Guest update(Guest guest);
    boolean delete(long id);
    Guest findById(long id);
    Guest findByIdNumber(String idNumber);
    List<Guest> findAll();
}
