package com.banking.repository;

import com.banking.constants.TransactionType;
import com.banking.entity.Account;
import com.banking.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findBySenderAccountOrReceiverAccountOrderByTimestampDesc(Account sender, Account receiver);

    @Query("SELECT t FROM Transaction t WHERE (t.senderAccount = :account OR t.receiverAccount = :account) AND t.timestamp >= :since ORDER BY t.timestamp DESC")
    List<Transaction> findRecentTransactionsForAccount(@Param("account") Account account, @Param("since") LocalDateTime since);

    @Query("SELECT t FROM Transaction t WHERE (t.senderAccount = :account OR t.receiverAccount = :account) AND t.timestamp BETWEEN :startDate AND :endDate ORDER BY t.timestamp DESC")
    List<Transaction> findByAccountAndDateRange(
        @Param("account") Account account,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT t FROM Transaction t WHERE t.timestamp >= :since ORDER BY t.timestamp DESC")
    List<Transaction> findAllSince(@Param("since") LocalDateTime since);

    long countByTimestampAfter(LocalDateTime since);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.transactionType = :type AND t.status = 'SUCCESS'")
    BigDecimal sumAmountByTransactionType(@Param("type") TransactionType type);

    List<Transaction> findTop10ByOrderByTimestampDesc();

    List<Transaction> findByTransactionIdContainingIgnoreCase(String transactionId);
}
