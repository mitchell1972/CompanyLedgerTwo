package com.companyledgertwo.service;

import com.companyledgertwo.model.Transaction;
import com.companyledgertwo.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateTransactionSuccessfully() {
        // Given
        Transaction transaction = new Transaction(1L, LocalDate.now(), 100.0);
        transaction.setId(1L);

        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // When
        Transaction createdTransaction = transactionService.createTransaction(transaction);

        // Then
        assertThat(createdTransaction).isNotNull();
        assertThat(createdTransaction.getAmount()).isEqualTo(100.0);
        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    void shouldFailToCreateTransactionWhenAmountIsNegative() {
        // Given
        Transaction transaction = new Transaction(1L, LocalDate.now(), -50.0); // Negative amount

        // When / Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createTransaction(transaction);
        });

        assertThat(exception.getMessage()).isEqualTo("Amount cannot be negative or null");
        verify(transactionRepository, times(0)).save(transaction);
    }

    @Test
    void shouldFailToCreateTransactionWhenAccountIdIsNull() {
        // Given
        Transaction transaction = new Transaction(null, LocalDate.now(), 100.0); // Null account ID

        // When / Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createTransaction(transaction);
        });

        assertThat(exception.getMessage()).isEqualTo("Account ID cannot be null");
        verify(transactionRepository, times(0)).save(transaction);
    }

    @Test
    void shouldFailToCreateTransactionWhenDateIsNull() {
        // Given
        Transaction transaction = new Transaction(1L, null, 100.0); // Null date

        // When / Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createTransaction(transaction);
        });

        assertThat(exception.getMessage()).isEqualTo("Transaction date cannot be null");
        verify(transactionRepository, times(0)).save(transaction);
    }

    @Test
    void shouldFindTransactionsByAccountId() {
        // Given
        Long accountId = 1L;
        Transaction transaction1 = new Transaction(1L, LocalDate.now(), 100.0);
        Transaction transaction2 = new Transaction(2L, LocalDate.now(), 150.0);

        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);

        when(transactionRepository.findByAccountId(accountId)).thenReturn(transactions);

        // When
        List<Transaction> foundTransactions = transactionService.findTransactionsByAccountId(accountId);

        // Then
        assertThat(foundTransactions).hasSize(2);
        verify(transactionRepository, times(1)).findByAccountId(accountId);
    }

    @Test
    void shouldFindTransactionsBetweenDates() {
        // Given
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        Transaction transaction1 = new Transaction(1L, LocalDate.of(2023, 2, 15), 200.0);
        Transaction transaction2 = new Transaction(2L, LocalDate.of(2023, 10, 10), 300.0);

        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);

        when(transactionRepository.findByDateBetween(startDate, endDate)).thenReturn(transactions);

        // When
        List<Transaction> foundTransactions = transactionService.findTransactionsBetweenDates(startDate, endDate);

        // Then
        assertThat(foundTransactions).hasSize(2);
        verify(transactionRepository, times(1)).findByDateBetween(startDate, endDate);
    }

    @Test
    void shouldFindTransactionsGreaterThan() {
        // Given
        Double minimumAmount = 200.0;

        Transaction transaction1 = new Transaction(1L, LocalDate.now(), 250.0);
        Transaction transaction2 = new Transaction(2L, LocalDate.now(), 300.0);

        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);

        when(transactionRepository.findByAmountGreaterThan(minimumAmount)).thenReturn(transactions);

        // When
        List<Transaction> foundTransactions = transactionService.findTransactionsGreaterThan(minimumAmount);

        // Then
        assertThat(foundTransactions).hasSize(2);
        verify(transactionRepository, times(1)).findByAmountGreaterThan(minimumAmount);
    }

    @Test
    void shouldFindTransactionsLessThan() {
        // Given
        Double maximumAmount = 200.0;

        Transaction transaction1 = new Transaction(1L, LocalDate.now(), 150.0);
        Transaction transaction2 = new Transaction(2L, LocalDate.now(), 100.0);

        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);

        when(transactionRepository.findByAmountLessThan(maximumAmount)).thenReturn(transactions);

        // When
        List<Transaction> foundTransactions = transactionService.findTransactionsLessThan(maximumAmount);

        // Then
        assertThat(foundTransactions).hasSize(2);
        verify(transactionRepository, times(1)).findByAmountLessThan(maximumAmount);
    }

    @Test
    void shouldReturnEmptyListWhenNoTransactionsFoundByAccountId() {
        // Given
        Long accountId = 999L;

        when(transactionRepository.findByAccountId(accountId)).thenReturn(Arrays.asList());

        // When
        List<Transaction> transactions = transactionService.findTransactionsByAccountId(accountId);

        // Then
        assertThat(transactions).isEmpty();
        verify(transactionRepository, times(1)).findByAccountId(accountId);
    }

    @Test
    void shouldReturnEmptyListWhenNoTransactionsFoundBetweenDates() {
        // Given
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        when(transactionRepository.findByDateBetween(startDate, endDate)).thenReturn(Arrays.asList());

        // When
        List<Transaction> transactions = transactionService.findTransactionsBetweenDates(startDate, endDate);

        // Then
        assertThat(transactions).isEmpty();
        verify(transactionRepository, times(1)).findByDateBetween(startDate, endDate);
    }

    @Test
    void shouldReturnEmptyListWhenNoTransactionsFoundGreaterThanAmount() {
        // Given
        Double minimumAmount = 1000.0;

        when(transactionRepository.findByAmountGreaterThan(minimumAmount)).thenReturn(Arrays.asList());

        // When
        List<Transaction> transactions = transactionService.findTransactionsGreaterThan(minimumAmount);

        // Then
        assertThat(transactions).isEmpty();
        verify(transactionRepository, times(1)).findByAmountGreaterThan(minimumAmount);
    }

    @Test
    void shouldReturnEmptyListWhenNoTransactionsFoundLessThanAmount() {
        // Given
        Double maximumAmount = 50.0;

        when(transactionRepository.findByAmountLessThan(maximumAmount)).thenReturn(Arrays.asList());

        // When
        List<Transaction> transactions = transactionService.findTransactionsLessThan(maximumAmount);

        // Then
        assertThat(transactions).isEmpty();
        verify(transactionRepository, times(1)).findByAmountLessThan(maximumAmount);
    }
}
