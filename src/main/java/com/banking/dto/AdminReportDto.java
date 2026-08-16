package com.banking.dto;

import java.math.BigDecimal;

public class AdminReportDto {

    private long totalCustomers;
    private long totalAccounts;
    private long activeAccounts;
    private long frozenAccounts;
    private long todayTransactionsCount;
    private BigDecimal totalBankDeposits;
    private BigDecimal totalTransfersVolume;
    private BigDecimal totalWithdrawalsVolume;

    public AdminReportDto() {}

    public long getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(long totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public long getTotalAccounts() {
        return totalAccounts;
    }

    public void setTotalAccounts(long totalAccounts) {
        this.totalAccounts = totalAccounts;
    }

    public long getActiveAccounts() {
        return activeAccounts;
    }

    public void setActiveAccounts(long activeAccounts) {
        this.activeAccounts = activeAccounts;
    }

    public long getFrozenAccounts() {
        return frozenAccounts;
    }

    public void setFrozenAccounts(long frozenAccounts) {
        this.frozenAccounts = frozenAccounts;
    }

    public long getTodayTransactionsCount() {
        return todayTransactionsCount;
    }

    public void setTodayTransactionsCount(long todayTransactionsCount) {
        this.todayTransactionsCount = todayTransactionsCount;
    }

    public BigDecimal getTotalBankDeposits() {
        return totalBankDeposits;
    }

    public void setTotalBankDeposits(BigDecimal totalBankDeposits) {
        this.totalBankDeposits = totalBankDeposits;
    }

    public BigDecimal getTotalTransfersVolume() {
        return totalTransfersVolume;
    }

    public void setTotalTransfersVolume(BigDecimal totalTransfersVolume) {
        this.totalTransfersVolume = totalTransfersVolume;
    }

    public BigDecimal getTotalWithdrawalsVolume() {
        return totalWithdrawalsVolume;
    }

    public void setTotalWithdrawalsVolume(BigDecimal totalWithdrawalsVolume) {
        this.totalWithdrawalsVolume = totalWithdrawalsVolume;
    }
}
