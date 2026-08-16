package com.banking.service;

import com.banking.dto.TransferRequestDto;
import com.banking.entity.Transaction;

public interface TransferService {
    Transaction transferFunds(String senderEmail, TransferRequestDto transferRequest);
}
