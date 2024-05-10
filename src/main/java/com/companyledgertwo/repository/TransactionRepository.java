package com.companyledgertwo.repository;

import com.companyledgertwo.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountId(Long accountId);
    List<Transaction> findByDateBetween(LocalDate startDate, LocalDate endDate);
    List<Transaction> findByAmountGreaterThan(Double minimumAmount);
    List<Transaction> findByAmountLessThan(Double maximumAmount);
}
