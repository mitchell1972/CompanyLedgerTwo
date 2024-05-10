package com.companyledgertwo.util;

import com.companyledgertwo.model.Transaction;

public class TransactionValidator {

    public static void validateTransaction(Transaction transaction) {
        if (transaction.getAccountId() == null) {
            throw new IllegalArgumentException("Account ID cannot be null");
        }
        if (transaction.getDate() == null) {
            throw new IllegalArgumentException("Transaction date cannot be null");
        }
        if (transaction.getAmount() == null || transaction.getAmount() < 0) {
            throw new IllegalArgumentException("Amount cannot be negative or null");
        }
    }
}
