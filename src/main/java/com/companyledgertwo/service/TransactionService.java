package com.companyledgertwo.service;

import com.companyledgertwo.model.Transaction;

import java.time.LocalDate;
import java.util.List;

public interface TransactionService {
    Transaction createTransaction(Transaction transaction);

    List<Transaction> findTransactionsByAccountId(Long accountId);

    List<Transaction> findTransactionsBetweenDates(LocalDate start, LocalDate end);

    List<Transaction> findTransactionsGreaterThan(Double amount);

    List<Transaction> findTransactionsLessThan(Double amount);
}
