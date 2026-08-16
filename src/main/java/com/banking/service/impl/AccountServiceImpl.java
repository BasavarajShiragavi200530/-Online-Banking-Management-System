package com.banking.service.impl;

import com.banking.constants.AccountStatus;
import com.banking.constants.AccountType;
import com.banking.constants.TransactionStatus;
import com.banking.constants.TransactionType;
import com.banking.dto.AccountDto;
import com.banking.dto.DepositWithdrawDto;
import com.banking.dto.EntityDtoMapper;
import com.banking.entity.Account;
import com.banking.entity.Transaction;
import com.banking.entity.User;
import com.banking.exception.AccountFrozenException;
import com.banking.exception.AccountNotFoundException;
import com.banking.exception.InsufficientBalanceException;
import com.banking.repository.AccountRepository;
import com.banking.repository.TransactionRepository;
import com.banking.service.AccountService;
import com.banking.service.AuditLogService;
import com.banking.util.AccountNumberGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AuditLogService auditLogService;

    @Value("${banking.app.ifsc-prefix:APEX000}")
    private String ifscPrefix;

    @Value("${banking.app.default-branch:Main Financial District Branch}")
    private String defaultBranch;

    public AccountServiceImpl(AccountRepository accountRepository,
                              TransactionRepository transactionRepository,
                              AuditLogService auditLogService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public Account createAccountForUser(User user, AccountType accountType, BigDecimal initialDeposit) {
        Account account = new Account();
        account.setAccountNumber(generateUniqueAccountNumber());
        account.setBalance(initialDeposit != null ? initialDeposit : BigDecimal.ZERO);
        account.setAccountType(accountType != null ? accountType : AccountType.SAVINGS);
        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setBranch(defaultBranch);
        account.setIfscCode(ifscPrefix + "101");
        account.setUser(user);

        Account savedAccount = accountRepository.save(account);

        // Record initial deposit transaction if > 0
        if (initialDeposit != null && initialDeposit.compareTo(BigDecimal.ZERO) > 0) {
            Transaction initTxn = new Transaction();
            initTxn.setTransactionId(AccountNumberGenerator.generateTransactionId());
            initTxn.setReceiverAccount(savedAccount);
            initTxn.setAmount(initialDeposit);
            initTxn.setTransactionType(TransactionType.DEPOSIT);
            initTxn.setStatus(TransactionStatus.SUCCESS);
            initTxn.setRemarks("Initial opening account deposit");
            transactionRepository.save(initTxn);
        }

        return savedAccount;
    }

    @Override
    @Transactional(readOnly = true)
    public Account getAccountByUserEmail(String email) {
        return accountRepository.findByUserEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new AccountNotFoundException("No bank account associated with email: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber.trim())
                .orElseThrow(() -> new AccountNotFoundException("Account number '" + accountNumber + "' not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDto getAccountDtoByEmail(String email) {
        Account account = getAccountByUserEmail(email);
        return EntityDtoMapper.toAccountDto(account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDto> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(EntityDtoMapper::toAccountDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateAccountStatus(Long accountId, AccountStatus status) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account ID " + accountId + " not found"));
        account.setAccountStatus(status);
        accountRepository.save(account);

        auditLogService.logAction("ADMIN", "ACCOUNT_STATUS_CHANGE",
                "Account " + account.getAccountNumber() + " status set to " + status, "127.0.0.1");
    }

    @Override
    @Transactional
    public void depositOrWithdraw(String userEmail, DepositWithdrawDto depositWithdrawDto) {
        Account account = getAccountByUserEmail(userEmail);

        if (account.getAccountStatus() == AccountStatus.FROZEN) {
            throw new AccountFrozenException("Transaction failed: Your account is currently FROZEN. Contact administration.");
        }

        BigDecimal amount = depositWithdrawDto.getAmount();

        Transaction txn = new Transaction();
        txn.setTransactionId(AccountNumberGenerator.generateTransactionId());
        txn.setAmount(amount);
        txn.setTransactionType(depositWithdrawDto.getTransactionType());
        txn.setRemarks(depositWithdrawDto.getRemarks() != null ? depositWithdrawDto.getRemarks() : depositWithdrawDto.getTransactionType().name());

        if (depositWithdrawDto.getTransactionType() == TransactionType.DEPOSIT) {
            account.setBalance(account.getBalance().add(amount));
            txn.setReceiverAccount(account);
            txn.setStatus(TransactionStatus.SUCCESS);
        } else if (depositWithdrawDto.getTransactionType() == TransactionType.WITHDRAWAL) {
            if (account.getBalance().compareTo(amount) < 0) {
                throw new InsufficientBalanceException("Insufficient funds! Available balance: $" + account.getBalance());
            }
            account.setBalance(account.getBalance().subtract(amount));
            txn.setSenderAccount(account);
            txn.setStatus(TransactionStatus.SUCCESS);
        }

        accountRepository.save(account);
        transactionRepository.save(txn);

        auditLogService.logAction(userEmail, depositWithdrawDto.getTransactionType().name(),
                depositWithdrawDto.getTransactionType() + " of $" + amount + " processed successfully", "127.0.0.1");
    }

    private String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            accountNumber = AccountNumberGenerator.generateAccountNumber();
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }
}
