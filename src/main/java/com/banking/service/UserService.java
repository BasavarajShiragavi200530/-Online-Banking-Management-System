package com.banking.service;

import com.banking.dto.ChangePasswordDto;
import com.banking.dto.RegisterDto;
import com.banking.dto.UserProfileDto;
import com.banking.entity.User;

import java.util.List;

public interface UserService {
    User registerUser(RegisterDto registerDto);
    User findByEmail(String email);
    boolean existsByEmail(String email);
    User updateProfile(String email, UserProfileDto profileDto);
    void changePassword(String email, ChangePasswordDto changePasswordDto);
    List<User> getAllUsers();
    void toggleUserStatus(Long userId);
}
