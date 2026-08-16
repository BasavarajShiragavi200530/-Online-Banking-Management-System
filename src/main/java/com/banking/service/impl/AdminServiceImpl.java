package com.banking.service.impl;

import com.banking.constants.AccountStatus;
import com.banking.constants.RoleName;
import com.banking.constants.TransactionType;
import com.banking.dto.AdminReportDto;
import com.banking.repository.AccountRepository;
import com.banking.repository.TransactionRepository;
import com.banking.repository.UserRepository;
import com.banking.service.AdminService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AdminServiceImpl(UserRepository userRepository,
                            AccountRepository accountRepository,
                            TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AdminReportDto generateAdminReport() {
        AdminReportDto report = new AdminReportDto();

        long totalUsers = userRepository.count();
        long totalAccounts = accountRepository.count();
        long activeAccounts = accountRepository.countByAccountStatus(AccountStatus.ACTIVE);
        long frozenAccounts = accountRepository.countByAccountStatus(AccountStatus.FROZEN);

        LocalDateTime startOfToday = LocalDateTime.now().toLocalDate().atStartOfDay();
        long todayTxns = transactionRepository.countByTimestampAfter(startOfToday);

        BigDecimal totalDeposits = accountRepository.getTotalBankDeposits();
        BigDecimal transferVolume = transactionRepository.sumAmountByTransactionType(TransactionType.TRANSFER);
        BigDecimal withdrawalVolume = transactionRepository.sumAmountByTransactionType(TransactionType.WITHDRAWAL);

        report.setTotalCustomers(totalUsers > 0 ? totalUsers - 1 : 0); // Exclude admin count
        report.setTotalAccounts(totalAccounts);
        report.setActiveAccounts(activeAccounts);
        report.setFrozenAccounts(frozenAccounts);
        report.setTodayTransactionsCount(todayTxns);
        report.setTotalBankDeposits(totalDeposits != null ? totalDeposits : BigDecimal.ZERO);
        report.setTotalTransfersVolume(transferVolume != null ? transferVolume : BigDecimal.ZERO);
        report.setTotalWithdrawalsVolume(withdrawalVolume != null ? withdrawalVolume : BigDecimal.ZERO);

        return report;
    }
}
