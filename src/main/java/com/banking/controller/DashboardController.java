package com.banking.controller;

import com.banking.dto.DashboardSummaryDto;
import com.banking.security.SecurityUtils;
import com.banking.service.TransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/customer")
public class DashboardController {

    private final TransactionService transactionService;

    public DashboardController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/dashboard")
    public String customerDashboard(Model model) {
        String email = SecurityUtils.getCurrentUserEmail();
        DashboardSummaryDto summary = transactionService.getDashboardSummaryForUser(email);
        model.addAttribute("dashboard", summary);
        return "customer/dashboard";
    }
}
