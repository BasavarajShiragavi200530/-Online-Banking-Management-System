package com.banking.service.impl;

import com.banking.dto.DashboardSummaryDto;
import com.banking.dto.EntityDtoMapper;
import com.banking.dto.TransactionDto;
import com.banking.entity.Account;
import com.banking.entity.Transaction;
import com.banking.repository.TransactionRepository;
import com.banking.service.AccountService;
import com.banking.service.TransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDto> getTransactionHistoryForUser(String email) {
        Account account = accountService.getAccountByUserEmail(email);
        List<Transaction> transactions = transactionRepository
                .findBySenderAccountOrReceiverAccountOrderByTimestampDesc(account, account);

        return transactions.stream()
                .map(txn -> EntityDtoMapper.toTransactionDto(txn, account))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDto> filterUserTransactions(String email, String timeframe, String searchTxnId) {
        Account account = accountService.getAccountByUserEmail(email);

        if (searchTxnId != null && !searchTxnId.isBlank()) {
            return transactionRepository.findByTransactionIdContainingIgnoreCase(searchTxnId.trim()).stream()
                    .filter(t -> (t.getSenderAccount() != null && t.getSenderAccount().getId().equals(account.getId())) ||
                            (t.getReceiverAccount() != null && t.getReceiverAccount().getId().equals(account.getId())))
                    .map(txn -> EntityDtoMapper.toTransactionDto(txn, account))
                    .collect(Collectors.toList());
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusYears(10); // default all time

        if ("today".equalsIgnoreCase(timeframe)) {
            startDate = now.toLocalDate().atStartOfDay();
        } else if ("weekly".equalsIgnoreCase(timeframe)) {
            startDate = now.minusDays(7);
        } else if ("monthly".equalsIgnoreCase(timeframe)) {
            startDate = now.minusMonths(1);
        }

        List<Transaction> transactions = transactionRepository.findByAccountAndDateRange(account, startDate, now);
        return transactions.stream()
                .map(txn -> EntityDtoMapper.toTransactionDto(txn, account))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDto> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(txn -> EntityDtoMapper.toTransactionDto(txn, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDto> searchTransactionsById(String query) {
        return transactionRepository.findByTransactionIdContainingIgnoreCase(query).stream()
                .map(txn -> EntityDtoMapper.toTransactionDto(txn, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryDto getDashboardSummaryForUser(String email) {
        Account account = accountService.getAccountByUserEmail(email);
        List<Transaction> allAccountTxns = transactionRepository
                .findBySenderAccountOrReceiverAccountOrderByTimestampDesc(account, account);

        BigDecimal moneyIn = BigDecimal.ZERO;
        BigDecimal moneyOut = BigDecimal.ZERO;

        for (Transaction t : allAccountTxns) {
            if (t.getReceiverAccount() != null && t.getReceiverAccount().getId().equals(account.getId())) {
                moneyIn = moneyIn.add(t.getAmount());
            } else if (t.getSenderAccount() != null && t.getSenderAccount().getId().equals(account.getId())) {
                moneyOut = moneyOut.add(t.getAmount());
            }
        }

        List<TransactionDto> recent = allAccountTxns.stream()
                .limit(5)
                .map(txn -> EntityDtoMapper.toTransactionDto(txn, account))
                .collect(Collectors.toList());

        DashboardSummaryDto summary = new DashboardSummaryDto();
        summary.setCustomerName(account.getUser().getFullName());
        summary.setAccountNumber(account.getAccountNumber());
        summary.setBalance(account.getBalance());
        summary.setAccountType(account.getAccountType().name());
        summary.setAccountStatus(account.getAccountStatus().name());
        summary.setBranch(account.getBranch());
        summary.setIfscCode(account.getIfscCode());
        summary.setTotalTransactionsCount(allAccountTxns.size());
        summary.setTotalMoneyIn(moneyIn);
        summary.setTotalMoneyOut(moneyOut);
        summary.setRecentTransactions(recent);

        return summary;
    }
}
