package com.oceanview.resort.service;

import com.oceanview.resort.dto.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO authenticate(String username, String password);
    UserDTO createUser(UserDTO dto, String rawPassword);
    UserDTO updateUser(UserDTO dto);
    boolean deleteUser(long id);
    UserDTO findById(long id);
    UserDTO findByUsername(String username);
    UserDTO findByEmail(String email);
    List<UserDTO> findAll();
    void requestPasswordResetOtp(String email);
    boolean resetPasswordWithOtp(String email, String otp, String newPassword);
}
