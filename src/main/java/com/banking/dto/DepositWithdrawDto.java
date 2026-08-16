package com.banking.dto;

import com.banking.constants.TransactionType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class DepositWithdrawDto {

    @NotNull(message = "Transaction type is required")
    private TransactionType transactionType;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "10.00", message = "Minimum transaction amount is $10.00")
    @DecimalMax(value = "50000.00", message = "Maximum single transaction limit is $50,000.00")
    private BigDecimal amount;

    @Size(max = 255, message = "Remarks cannot exceed 255 characters")
    private String remarks;

    public DepositWithdrawDto() {}

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
