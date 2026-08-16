package com.banking.service.impl;

import com.banking.constants.RoleName;
import com.banking.dto.ChangePasswordDto;
import com.banking.dto.RegisterDto;
import com.banking.dto.UserProfileDto;
import com.banking.entity.Role;
import com.banking.entity.User;
import com.banking.exception.DuplicateEmailException;
import com.banking.exception.InvalidTransactionException;
import com.banking.repository.RoleRepository;
import com.banking.repository.UserRepository;
import com.banking.service.AccountService;
import com.banking.service.AuditLogService;
import com.banking.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           AccountService accountService,
                           PasswordEncoder passwordEncoder,
                           AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public User registerUser(RegisterDto registerDto) {
        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            throw new IllegalArgumentException("Password and confirm password do not match");
        }

        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new DuplicateEmailException("Email address '" + registerDto.getEmail() + "' is already registered");
        }

        User user = new User();
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setEmail(registerDto.getEmail().toLowerCase().trim());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setPhone(registerDto.getPhone());
        user.setAddress(registerDto.getAddress());
        user.setEnabled(true);

        Role customerRole = roleRepository.findByName(RoleName.ROLE_CUSTOMER)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.ROLE_CUSTOMER)));
        user.setRoles(Collections.singleton(customerRole));

        User savedUser = userRepository.save(user);

        // Auto-create Bank Account for Customer
        accountService.createAccountForUser(savedUser, registerDto.getAccountType(), registerDto.getInitialDeposit());

        auditLogService.logAction(savedUser.getEmail(), "USER_REGISTERED",
                "New user registered with initial deposit $" + registerDto.getInitialDeposit(), "127.0.0.1");

        return savedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email.toLowerCase().trim());
    }

    @Override
    @Transactional
    public User updateProfile(String email, UserProfileDto profileDto) {
        User user = findByEmail(email);
        user.setFirstName(profileDto.getFirstName());
        user.setLastName(profileDto.getLastName());
        user.setPhone(profileDto.getPhone());
        user.setAddress(profileDto.getAddress());

        User updated = userRepository.save(user);
        auditLogService.logAction(email, "PROFILE_UPDATE", "User updated personal profile details", "127.0.0.1");
        return updated;
    }

    @Override
    @Transactional
    public void changePassword(String email, ChangePasswordDto changePasswordDto) {
        User user = findByEmail(email);

        if (!passwordEncoder.matches(changePasswordDto.getCurrentPassword(), user.getPassword())) {
            throw new InvalidTransactionException("Current password provided is incorrect");
        }

        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmPassword())) {
            throw new InvalidTransactionException("New password and confirm password do not match");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        userRepository.save(user);

        auditLogService.logAction(email, "PASSWORD_CHANGED", "User successfully changed password", "127.0.0.1");
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User ID " + userId + " not found"));
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);

        auditLogService.logAction("ADMIN", "USER_STATUS_TOGGLED",
                "User " + user.getEmail() + " status set to " + (user.isEnabled() ? "ENABLED" : "DISABLED"), "127.0.0.1");
    }
}
