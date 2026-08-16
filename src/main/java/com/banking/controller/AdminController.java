package com.banking.controller;

import com.banking.constants.AccountStatus;
import com.banking.dto.AccountDto;
import com.banking.dto.AdminReportDto;
import com.banking.dto.TransactionDto;
import com.banking.entity.AuditLog;
import com.banking.entity.User;
import com.banking.service.AccountService;
import com.banking.service.AdminService;
import com.banking.service.AuditLogService;
import com.banking.service.TransactionService;
import com.banking.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final AuditLogService auditLogService;

    public AdminController(AdminService adminService,
                           UserService userService,
                           AccountService accountService,
                           TransactionService transactionService,
                           AuditLogService auditLogService) {
        this.adminService = adminService;
        this.userService = userService;
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.auditLogService = auditLogService;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        AdminReportDto report = adminService.generateAdminReport();
        List<AccountDto> accounts = accountService.getAllAccounts();
        List<TransactionDto> recentTxns = transactionService.getAllTransactions();
        if (recentTxns.size() > 10) {
            recentTxns = recentTxns.subList(0, 10);
        }

        model.addAttribute("report", report);
        model.addAttribute("accounts", accounts);
        model.addAttribute("recentTransactions", recentTxns);
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable("id") Long userId, RedirectAttributes redirectAttributes) {
        userService.toggleUserStatus(userId);
        redirectAttributes.addFlashAttribute("successMessage", "User access status updated successfully!");
        return "redirect:/admin/users";
    }

    @GetMapping("/accounts")
    public String manageAccounts(Model model) {
        List<AccountDto> accounts = accountService.getAllAccounts();
        model.addAttribute("accounts", accounts);
        return "admin/accounts";
    }

    @PostMapping("/accounts/{id}/freeze")
    public String freezeAccount(@PathVariable("id") Long accountId, RedirectAttributes redirectAttributes) {
        accountService.updateAccountStatus(accountId, AccountStatus.FROZEN);
        redirectAttributes.addFlashAttribute("successMessage", "Account successfully FROZEN.");
        return "redirect:/admin/accounts";
    }

    @PostMapping("/accounts/{id}/activate")
    public String activateAccount(@PathVariable("id") Long accountId, RedirectAttributes redirectAttributes) {
        accountService.updateAccountStatus(accountId, AccountStatus.ACTIVE);
        redirectAttributes.addFlashAttribute("successMessage", "Account successfully ACTIVATED.");
        return "redirect:/admin/accounts";
    }

    @GetMapping("/transactions")
    public String manageTransactions(@RequestParam(value = "query", required = false) String query, Model model) {
        List<TransactionDto> transactions;
        if (query != null && !query.isBlank()) {
            transactions = transactionService.searchTransactionsById(query.trim());
        } else {
            transactions = transactionService.getAllTransactions();
        }
        model.addAttribute("transactions", transactions);
        model.addAttribute("searchQuery", query);
        return "admin/transactions";
    }

    @GetMapping("/reports")
    public String viewReports(Model model) {
        AdminReportDto report = adminService.generateAdminReport();
        model.addAttribute("report", report);
        return "admin/reports";
    }

    @GetMapping("/audit-logs")
    public String viewAuditLogs(Model model) {
        List<AuditLog> auditLogs = auditLogService.getRecentAuditLogs();
        model.addAttribute("auditLogs", auditLogs);
        return "admin/audit-logs";
    }
}
