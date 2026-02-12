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

    /**
     * Sends a password-reset OTP to the user's email if the account exists and has an email.
     * Always returns true (no email enumeration) when email is non-blank.
     */
    boolean requestPasswordResetOtp(String email);

    /**
     * Resets password using the OTP sent to the given email. Returns true if OTP was valid and password updated.
     */
    boolean resetPasswordWithOtp(String email, String otp, String newPassword);
}
