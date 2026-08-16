package com.banking.dto;

import java.math.BigDecimal;
import java.util.List;

public class DashboardSummaryDto {

    private String customerName;
    private String accountNumber;
    private BigDecimal balance;
    private String accountType;
    private String accountStatus;
    private String branch;
    private String ifscCode;
    private long totalTransactionsCount;
    private BigDecimal totalMoneyIn;
    private BigDecimal totalMoneyOut;
    private List<TransactionDto> recentTransactions;

    public DashboardSummaryDto() {}

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public long getTotalTransactionsCount() {
        return totalTransactionsCount;
    }

    public void setTotalTransactionsCount(long totalTransactionsCount) {
        this.totalTransactionsCount = totalTransactionsCount;
    }

    public BigDecimal getTotalMoneyIn() {
        return totalMoneyIn;
    }

    public void setTotalMoneyIn(BigDecimal totalMoneyIn) {
        this.totalMoneyIn = totalMoneyIn;
    }

    public BigDecimal getTotalMoneyOut() {
        return totalMoneyOut;
    }

    public void setTotalMoneyOut(BigDecimal totalMoneyOut) {
        this.totalMoneyOut = totalMoneyOut;
    }

    public List<TransactionDto> getRecentTransactions() {
        return recentTransactions;
    }

    public void setRecentTransactions(List<TransactionDto> recentTransactions) {
        this.recentTransactions = recentTransactions;
    }
}
