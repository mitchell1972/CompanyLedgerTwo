package com.companyledgertwo.service;

import com.companyledgertwo.model.Transaction;
import com.companyledgertwo.repository.TransactionRepository;
import com.companyledgertwo.util.TransactionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public Transaction createTransaction(Transaction transaction) {
        TransactionValidator.validateTransaction(transaction);
        return transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> findTransactionsByAccountId(Long accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    @Override
    public List<Transaction> findTransactionsBetweenDates(LocalDate start, LocalDate end) {
        return transactionRepository.findByDateBetween(start, end);
    }

    @Override
    public List<Transaction> findTransactionsGreaterThan(Double amount) {
        return transactionRepository.findByAmountGreaterThan(amount);
    }

    @Override
    public List<Transaction> findTransactionsLessThan(Double amount) {
        return transactionRepository.findByAmountLessThan(amount);
    }
}
