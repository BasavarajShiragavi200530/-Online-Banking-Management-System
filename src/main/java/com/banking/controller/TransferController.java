package com.banking.controller;

import com.banking.dto.AccountDto;
import com.banking.dto.TransferRequestDto;
import com.banking.security.SecurityUtils;
import com.banking.service.AccountService;
import com.banking.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customer")
public class TransferController {

    private final TransferService transferService;
    private final AccountService accountService;

    public TransferController(TransferService transferService, AccountService accountService) {
        this.transferService = transferService;
        this.accountService = accountService;
    }

    @GetMapping("/transfer")
    public String showTransferForm(Model model) {
        String email = SecurityUtils.getCurrentUserEmail();
        AccountDto account = accountService.getAccountDtoByEmail(email);
        model.addAttribute("account", account);

        if (!model.containsAttribute("transferRequestDto")) {
            model.addAttribute("transferRequestDto", new TransferRequestDto());
        }
        return "customer/transfer";
    }

    @PostMapping("/transfer")
    public String executeTransfer(@Valid @ModelAttribute("transferRequestDto") TransferRequestDto transferRequestDto,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {

        String email = SecurityUtils.getCurrentUserEmail();
        AccountDto account = accountService.getAccountDtoByEmail(email);
        model.addAttribute("account", account);

        if (bindingResult.hasErrors()) {
            return "customer/transfer";
        }

        try {
            transferService.transferFunds(email, transferRequestDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Success! Transferred $" + transferRequestDto.getAmount() + " to account #" + transferRequestDto.getRecipientAccountNumber());
            return "redirect:/customer/dashboard";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/customer/transfer";
        }
    }
}
