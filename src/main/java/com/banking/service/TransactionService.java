package com.banking.service;

import com.banking.dto.DashboardSummaryDto;
import com.banking.dto.TransactionDto;
import com.banking.entity.Account;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    List<TransactionDto> getTransactionHistoryForUser(String email);
    List<TransactionDto> filterUserTransactions(String email, String timeframe, String searchTxnId);
    List<TransactionDto> getAllTransactions();
    List<TransactionDto> searchTransactionsById(String query);
    DashboardSummaryDto getDashboardSummaryForUser(String email);
}
