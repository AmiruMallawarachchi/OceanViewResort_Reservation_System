package com.oceanview.resort.mapper;

import com.oceanview.resort.dto.UserDTO;
import com.oceanview.resort.model.User;
import com.oceanview.resort.model.enums.UserRole;

public final class UserMapper {
    private UserMapper() {
    }

    public static UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole() == null ? null : user.getRole().name());
        dto.setActive(user.isActive());
        return dto;
    }

    public static User toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        if (dto.getRole() != null) {
            user.setRole(UserRole.valueOf(dto.getRole()));
        }
        user.setActive(dto.isActive());
        return user;
    }
}
