package com.banking.service;

import com.banking.dto.AccountDto;
import com.banking.dto.DepositWithdrawDto;
import com.banking.entity.Account;
import com.banking.entity.User;

import java.util.List;

public interface AccountService {
    Account createAccountForUser(User user, com.banking.constants.AccountType accountType, java.math.BigDecimal initialDeposit);
    Account getAccountByUserEmail(String email);
    Account getAccountByNumber(String accountNumber);
    AccountDto getAccountDtoByEmail(String email);
    List<AccountDto> getAllAccounts();
    void updateAccountStatus(Long accountId, com.banking.constants.AccountStatus status);
    void depositOrWithdraw(String userEmail, DepositWithdrawDto depositWithdrawDto);
}
