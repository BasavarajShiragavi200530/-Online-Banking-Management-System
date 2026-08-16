package com.banking.service.impl;

import com.banking.constants.AccountStatus;
import com.banking.constants.TransactionStatus;
import com.banking.constants.TransactionType;
import com.banking.dto.TransferRequestDto;
import com.banking.entity.Account;
import com.banking.entity.Transaction;
import com.banking.exception.AccountFrozenException;
import com.banking.exception.AccountNotFoundException;
import com.banking.exception.InsufficientBalanceException;
import com.banking.exception.InvalidTransactionException;
import com.banking.repository.AccountRepository;
import com.banking.repository.TransactionRepository;
import com.banking.service.AccountService;
import com.banking.service.AuditLogService;
import com.banking.service.TransferService;
import com.banking.util.AccountNumberGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransferServiceImpl implements TransferService {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AuditLogService auditLogService;

    public TransferServiceImpl(AccountService accountService,
                               AccountRepository accountRepository,
                               TransactionRepository transactionRepository,
                               AuditLogService auditLogService) {
        this.accountService = accountService;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public Transaction transferFunds(String senderEmail, TransferRequestDto transferRequest) {
        Account senderAccount = accountService.getAccountByUserEmail(senderEmail);
        Account receiverAccount = accountService.getAccountByNumber(transferRequest.getRecipientAccountNumber());

        // Validation Checks
        if (senderAccount.getId().equals(receiverAccount.getId())) {
            throw new InvalidTransactionException("Self-transfer invalid! Receiver account cannot be your own account.");
        }

        if (senderAccount.getAccountStatus() == AccountStatus.FROZEN) {
            throw new AccountFrozenException("Transfer rejected: Your account is FROZEN.");
        }

        if (receiverAccount.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new AccountFrozenException("Transfer rejected: Target recipient account is inactive or frozen.");
        }

        BigDecimal amount = transferRequest.getAmount();

        if (senderAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient account balance. Available: $" + senderAccount.getBalance());
        }

        // Debit Sender, Credit Receiver
        senderAccount.setBalance(senderAccount.getBalance().subtract(amount));
        receiverAccount.setBalance(receiverAccount.getBalance().add(amount));

        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);

        // Record Transaction
        Transaction transaction = new Transaction();
        transaction.setTransactionId(AccountNumberGenerator.generateTransactionId());
        transaction.setSenderAccount(senderAccount);
        transaction.setReceiverAccount(receiverAccount);
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setRemarks(transferRequest.getRemarks() != null && !transferRequest.getRemarks().isBlank() ?
                transferRequest.getRemarks() : "Funds transfer to " + receiverAccount.getAccountNumber());

        Transaction savedTxn = transactionRepository.save(transaction);

        auditLogService.logAction(senderEmail, "FUND_TRANSFER",
                "Transferred $" + amount + " to Account #" + receiverAccount.getAccountNumber(), "127.0.0.1");

        return savedTxn;
    }
}
