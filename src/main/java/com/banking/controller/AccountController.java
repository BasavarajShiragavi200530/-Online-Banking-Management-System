package com.banking.controller;

import com.banking.dto.AccountDto;
import com.banking.dto.ChangePasswordDto;
import com.banking.dto.DepositWithdrawDto;
import com.banking.dto.EntityDtoMapper;
import com.banking.dto.UserProfileDto;
import com.banking.entity.User;
import com.banking.security.SecurityUtils;
import com.banking.service.AccountService;
import com.banking.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customer")
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;

    public AccountController(AccountService accountService, UserService userService) {
        this.accountService = accountService;
        this.userService = userService;
    }

    @GetMapping("/account-details")
    public String viewAccountDetails(Model model) {
        String email = SecurityUtils.getCurrentUserEmail();
        AccountDto account = accountService.getAccountDtoByEmail(email);
        model.addAttribute("account", account);
        return "customer/account-details";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        String email = SecurityUtils.getCurrentUserEmail();
        User user = userService.findByEmail(email);
        if (!model.containsAttribute("profileDto")) {
            model.addAttribute("profileDto", EntityDtoMapper.toUserProfileDto(user));
        }
        return "customer/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("profileDto") UserProfileDto profileDto,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "customer/profile";
        }

        String email = SecurityUtils.getCurrentUserEmail();
        userService.updateProfile(email, profileDto);
        redirectAttributes.addFlashAttribute("successMessage", "Profile details updated successfully!");
        return "redirect:/customer/profile";
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model) {
        if (!model.containsAttribute("changePasswordDto")) {
            model.addAttribute("changePasswordDto", new ChangePasswordDto());
        }
        return "customer/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute("changePasswordDto") ChangePasswordDto changePasswordDto,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "customer/change-password";
        }

        String email = SecurityUtils.getCurrentUserEmail();
        try {
            userService.changePassword(email, changePasswordDto);
            redirectAttributes.addFlashAttribute("successMessage", "Password updated successfully!");
            return "redirect:/customer/change-password";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/customer/change-password";
        }
    }

    @GetMapping("/deposit-withdraw")
    public String showDepositWithdrawForm(Model model) {
        String email = SecurityUtils.getCurrentUserEmail();
        AccountDto account = accountService.getAccountDtoByEmail(email);
        model.addAttribute("account", account);
        if (!model.containsAttribute("depositWithdrawDto")) {
            model.addAttribute("depositWithdrawDto", new DepositWithdrawDto());
        }
        return "customer/deposit-withdraw";
    }

    @PostMapping("/deposit-withdraw")
    public String processDepositWithdraw(@Valid @ModelAttribute("depositWithdrawDto") DepositWithdrawDto dto,
                                         BindingResult bindingResult,
                                         RedirectAttributes redirectAttributes,
                                         Model model) {

        String email = SecurityUtils.getCurrentUserEmail();
        AccountDto account = accountService.getAccountDtoByEmail(email);
        model.addAttribute("account", account);

        if (bindingResult.hasErrors()) {
            return "customer/deposit-withdraw";
        }

        try {
            accountService.depositOrWithdraw(email, dto);
            redirectAttributes.addFlashAttribute("successMessage",
                    dto.getTransactionType() + " of $" + dto.getAmount() + " processed successfully!");
            return "redirect:/customer/dashboard";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/customer/deposit-withdraw";
        }
    }
}
