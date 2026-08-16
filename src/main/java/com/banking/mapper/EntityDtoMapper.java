package com.banking.dto;

import com.banking.entity.Account;
import com.banking.entity.Transaction;
import com.banking.entity.User;
import com.banking.util.DateUtils;

public class EntityDtoMapper {

    public static AccountDto toAccountDto(Account account) {
        if (account == null) return null;

        AccountDto dto = new AccountDto();
        dto.setId(account.getId());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setBalance(account.getBalance());
        dto.setAccountType(account.getAccountType());
        dto.setAccountStatus(account.getAccountStatus());
        dto.setBranch(account.getBranch());
        dto.setIfscCode(account.getIfscCode());
        dto.setCreatedAt(account.getCreatedAt());

        if (account.getUser() != null) {
            dto.setOwnerName(account.getUser().getFullName());
            dto.setOwnerEmail(account.getUser().getEmail());
        }

        return dto;
    }

    public static TransactionDto toTransactionDto(Transaction transaction, Account currentAccount) {
        if (transaction == null) return null;

        TransactionDto dto = new TransactionDto();
        dto.setId(transaction.getId());
        dto.setTransactionId(transaction.getTransactionId());
        dto.setAmount(transaction.getAmount());
        dto.setTransactionType(transaction.getTransactionType());
        dto.setStatus(transaction.getStatus());
        dto.setRemarks(transaction.getRemarks());
        dto.setTimestamp(transaction.getTimestamp());

        if (transaction.getSenderAccount() != null) {
            dto.setSenderAccountNumber(transaction.getSenderAccount().getAccountNumber());
            if (transaction.getSenderAccount().getUser() != null) {
                dto.setSenderName(transaction.getSenderAccount().getUser().getFullName());
            }
        } else {
            dto.setSenderAccountNumber("SYSTEM / DEPOSIT");
            dto.setSenderName("Apex Central Reserve");
        }

        if (transaction.getReceiverAccount() != null) {
            dto.setReceiverAccountNumber(transaction.getReceiverAccount().getAccountNumber());
            if (transaction.getReceiverAccount().getUser() != null) {
                dto.setReceiverName(transaction.getReceiverAccount().getUser().getFullName());
            }
        } else {
            dto.setReceiverAccountNumber("SYSTEM / WITHDRAWAL");
            dto.setReceiverName("Cash Withdrawal");
        }

        if (currentAccount != null) {
            boolean isReceiver = transaction.getReceiverAccount() != null &&
                    transaction.getReceiverAccount().getId().equals(currentAccount.getId());
            dto.setCredit(isReceiver);
        }

        return dto;
    }

    public static UserProfileDto toUserProfileDto(User user) {
        if (user == null) return null;

        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());

        return dto;
    }
}
