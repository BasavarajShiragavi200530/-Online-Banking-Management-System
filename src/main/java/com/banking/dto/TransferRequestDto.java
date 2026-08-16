package com.banking.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class TransferRequestDto {

    @NotBlank(message = "Recipient account number is required")
    @Size(min = 10, max = 20, message = "Account number must be between 10 and 20 digits")
    private String recipientAccountNumber;

    @NotNull(message = "Transfer amount is required")
    @DecimalMin(value = "1.00", message = "Minimum transfer amount is $1.00")
    @DecimalMax(value = "100000.00", message = "Maximum per-transaction transfer limit is $100,000.00")
    private BigDecimal amount;

    @Size(max = 255, message = "Remarks cannot exceed 255 characters")
    private String remarks;

    public TransferRequestDto() {}

    public String getRecipientAccountNumber() {
        return recipientAccountNumber;
    }

    public void setRecipientAccountNumber(String recipientAccountNumber) {
        this.recipientAccountNumber = recipientAccountNumber;
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
