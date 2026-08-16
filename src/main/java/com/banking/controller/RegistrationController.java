package com.banking.controller;

import com.banking.dto.RegisterDto;
import com.banking.exception.DuplicateEmailException;
import com.banking.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistrationController {

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        if (!model.containsAttribute("registerDto")) {
            model.addAttribute("registerDto", new RegisterDto());
        }
        return "public/register";
    }

    @PostMapping("/register")
    public String registerUserAccount(@Valid @ModelAttribute("registerDto") RegisterDto registerDto,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {

        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.registerDto", "Password and confirmation do not match.");
        }

        if (userService.existsByEmail(registerDto.getEmail())) {
            bindingResult.rejectValue("email", "error.registerDto", "An account with this email address already exists.");
        }

        if (bindingResult.hasErrors()) {
            return "public/register";
        }

        try {
            userService.registerUser(registerDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Registration successful! Your new bank account has been created. Please log in.");
            return "redirect:/login";
        } catch (DuplicateEmailException ex) {
            bindingResult.rejectValue("email", "error.registerDto", ex.getMessage());
            return "public/register";
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "Registration failed: " + ex.getMessage());
            return "public/register";
        }
    }
}
