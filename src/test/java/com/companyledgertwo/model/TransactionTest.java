package com.companyledgertwo.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateValidTransaction() {
        // Given
        Transaction transaction = new Transaction(1L, LocalDate.now(), 100.0);

        // When
        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailValidationWhenAccountIdIsNull() {
        // Given
        Transaction transaction = new Transaction(null, LocalDate.now(), 100.0);

        // When
        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<Transaction> violation = violations.iterator().next();
        assertEquals("Account ID cannot be null", violation.getMessage());
    }

    @Test
    void shouldFailValidationWhenTransactionDateIsNull() {
        // Given
        Transaction transaction = new Transaction(1L, null, 100.0);

        // When
        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<Transaction> violation = violations.iterator().next();
        assertEquals("Transaction date cannot be null", violation.getMessage());
    }

    @Test
    void shouldFailValidationWhenAmountIsNull() {
        // Given
        Transaction transaction = new Transaction(1L, LocalDate.now(), null);

        // When
        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<Transaction> violation = violations.iterator().next();
        assertEquals("Amount cannot be null", violation.getMessage());
    }

    @Test
    void shouldFailValidationWhenAmountIsNegative() {
        // Given
        Transaction transaction = new Transaction(1L, LocalDate.now(), -100.0);

        // When
        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<Transaction> violation = violations.iterator().next();
        assertEquals("Amount cannot be negative", violation.getMessage());
    }

    @Test
    void shouldSetAndGetIdSuccessfully() {
        // Given
        Transaction transaction = new Transaction(1L, LocalDate.now(), 100.0);

        // When
        transaction.setId(1L);

        // Then
        assertThat(transaction.getId()).isEqualTo(1L);
    }

    @Test
    void shouldSetAndGetAccountIdSuccessfully() {
        // Given
        Transaction transaction = new Transaction(1L, LocalDate.now(), 100.0);

        // When
        transaction.setAccountId(2L);

        // Then
        assertThat(transaction.getAccountId()).isEqualTo(2L);
    }

    @Test
    void shouldSetAndGetDateSuccessfully() {
        // Given
        Transaction transaction = new Transaction(1L, LocalDate.now(), 100.0);
        LocalDate newDate = LocalDate.of(2024, 5, 1);

        // When
        transaction.setDate(newDate);

        // Then
        assertThat(transaction.getDate()).isEqualTo(newDate);
    }

    @Test
    void shouldSetAndGetAmountSuccessfully() {
        // Given
        Transaction transaction = new Transaction(1L, LocalDate.now(), 100.0);

        // When
        transaction.setAmount(200.0);

        // Then
        assertThat(transaction.getAmount()).isEqualTo(200.0);
    }
}
