package com.banking.controller;

import com.banking.dto.AccountDto;
import com.banking.dto.TransactionDto;
import com.banking.security.SecurityUtils;
import com.banking.service.AccountService;
import com.banking.service.TransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/customer")
public class TransactionController {

    private final TransactionService transactionService;
    private final AccountService accountService;

    public TransactionController(TransactionService transactionService, AccountService accountService) {
        this.transactionService = transactionService;
        this.accountService = accountService;
    }

    @GetMapping("/transactions")
    public String viewTransactionHistory(@RequestParam(value = "timeframe", defaultValue = "all") String timeframe,
                                         @RequestParam(value = "search", required = false) String search,
                                         Model model) {

        String email = SecurityUtils.getCurrentUserEmail();
        AccountDto account = accountService.getAccountDtoByEmail(email);
        List<TransactionDto> transactions = transactionService.filterUserTransactions(email, timeframe, search);

        model.addAttribute("account", account);
        model.addAttribute("transactions", transactions);
        model.addAttribute("currentTimeframe", timeframe);
        model.addAttribute("searchQuery", search);

        return "customer/transactions";
    }
}
