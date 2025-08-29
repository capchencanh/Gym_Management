// src/main/java/com/dhd/gymmanagement/mapper/UserMapper.java
package com.dhd.gymmanagement.mapper;

import com.dhd.gymmanagement.dto.UserDTO;
import com.dhd.gymmanagement.entity.User;

public class UserMapper {

    public static UserDTO toUserDto(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRole(user.getRole().name());
        dto.setName(user.getName());
        dto.setGender(user.getGender());
        dto.setBirthdate(user.getBirthdate());
        dto.setHeight(user.getHeight());
        dto.setWeight(user.getWeight());
        dto.setFitnessGoal(user.getFitnessGoal());

        return dto;
    }
}