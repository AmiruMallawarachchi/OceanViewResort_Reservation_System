package com.oceanview.resort.service.impl;

import com.oceanview.resort.dto.UserDTO;
import com.oceanview.resort.mapper.UserMapper;
import com.oceanview.resort.service.EmailService;
import com.oceanview.resort.model.User;
import com.oceanview.resort.repository.UserRepository;
import com.oceanview.resort.service.UserService;
import com.oceanview.resort.security.PasswordResetOtpStore;
import com.oceanview.resort.security.PasswordUtil;
import com.oceanview.resort.security.ValidationUtil;

import java.util.List;
import java.util.stream.Collectors;

public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordResetOtpStore otpStore;
    private final EmailService emailService;

    public UserServiceImpl(UserRepository userRepository) {
        this(userRepository, new PasswordResetOtpStore(), new EmailService());
    }

    public UserServiceImpl(UserRepository userRepository, PasswordResetOtpStore otpStore, EmailService emailService) {
        this.userRepository = userRepository;
        this.otpStore = otpStore;
        this.emailService = emailService;
    }

    @Override
    public UserDTO authenticate(String username, String password) {
        ValidationUtil.requireNonBlank(username, "Username is required");
        ValidationUtil.requireNonBlank(password, "Password is required");
        User user = userRepository.findByUsername(username);
        if (user == null || !user.isActive()) {
            return null;
        }
        if (!PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
            return null;
        }
        return UserMapper.toDTO(user);
    }

    @Override
    public UserDTO createUser(UserDTO dto, String rawPassword) {
        ValidationUtil.requireNonBlank(dto.getUsername(), "Username is required");
        ValidationUtil.requireNonBlank(dto.getRole(), "Role is required");
        ValidationUtil.requireNonBlank(rawPassword, "Password is required");
        User user = UserMapper.toEntity(dto);
        user.setPasswordHash(PasswordUtil.hashPassword(rawPassword));
        user.setActive(true);
        return UserMapper.toDTO(userRepository.create(user));
    }

    @Override
    public UserDTO updateUser(UserDTO dto) {
        ValidationUtil.requireNonBlank(dto.getRole(), "Role is required");
        User user = UserMapper.toEntity(dto);
        User existing = userRepository.findById(user.getId());
        if (existing == null) {
            return null;
        }
        user.setPasswordHash(existing.getPasswordHash());
        return UserMapper.toDTO(userRepository.update(user));
    }

    @Override
    public boolean deleteUser(long id) {
        return userRepository.delete(id);
    }

    @Override
    public UserDTO findById(long id) {
        return UserMapper.toDTO(userRepository.findById(id));
    }

    @Override
    public UserDTO findByUsername(String username) {
        return UserMapper.toDTO(userRepository.findByUsername(username));
    }

    @Override
    public UserDTO findByEmail(String email) {
        return UserMapper.toDTO(userRepository.findByEmail(email));
    }

    @Override
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream().map(UserMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public boolean requestPasswordResetOtp(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        User user = userRepository.findByEmail(email.trim());
        if (user == null || !user.isActive() || user.getEmail() == null || user.getEmail().isBlank()) {
            return true; // Do not reveal whether email exists
        }
        String otp = otpStore.generateAndStore(user.getEmail());
        if (otp != null) {
            emailService.sendPasswordResetOtp(user.getEmail(), otp);
        }
        return true;
    }

    @Override
    public boolean resetPasswordWithOtp(String email, String otp, String newPassword) {
        if (email == null || email.isBlank() || otp == null || otp.isBlank()) {
            return false;
        }
        ValidationUtil.requireNonBlank(newPassword, "New password is required");
        User user = userRepository.findByEmail(email.trim());
        if (user == null || !user.isActive()) {
            return false;
        }
        String emailToVerify = user.getEmail();
        if (emailToVerify == null || emailToVerify.isBlank()) {
            return false;
        }
        if (!otpStore.verifyAndConsume(emailToVerify, otp)) {
            return false;
        }
        user.setPasswordHash(PasswordUtil.hashPassword(newPassword));
        userRepository.update(user);
        return true;
    }
}
