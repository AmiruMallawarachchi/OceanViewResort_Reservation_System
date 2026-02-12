package com.oceanview.resort.repository;

import com.oceanview.resort.model.User;

import java.util.List;

public interface UserRepository {
    User create(User user);
    User update(User user);
    boolean delete(long id);
    User findById(long id);
    User findByUsername(String username);
    User findByEmail(String email);
    List<User> findAll();
}
