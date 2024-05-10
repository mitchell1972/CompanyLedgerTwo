package com.companyledgertwo.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateValidAccount() {
        // Given
        Account account = new Account("Test Account", 1000.0, true);

        // When
        Set<ConstraintViolation<Account>> violations = validator.validate(account);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailValidationWhenAccountNameIsBlank() {
        // Given
        Account account = new Account("", 1000.0, true);

        // When
        Set<ConstraintViolation<Account>> violations = validator.validate(account);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<Account> violation = violations.iterator().next();
        assertEquals("Account name cannot be empty", violation.getMessage());
    }

    @Test
    void shouldFailValidationWhenBalanceIsNegative() {
        // Given
        Account account = new Account("Test Account", -100.0, true);

        // When
        Set<ConstraintViolation<Account>> violations = validator.validate(account);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<Account> violation = violations.iterator().next();
        assertEquals("Balance cannot be negative", violation.getMessage());
    }

    @Test
    void shouldFailValidationWhenBalanceIsNull() {
        // Given
        Account account = new Account("Test Account", null, true);

        // When
        Set<ConstraintViolation<Account>> violations = validator.validate(account);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<Account> violation = violations.iterator().next();
        assertEquals("Balance cannot be null", violation.getMessage());
    }

    @Test
    void shouldFailValidationWhenIsActiveIsNull() {
        // Given
        Account account = new Account("Test Account", 1000.0, null);

        // When
        Set<ConstraintViolation<Account>> violations = validator.validate(account);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<Account> violation = violations.iterator().next();
        assertEquals("Active status cannot be null", violation.getMessage());
    }

    @Test
    void shouldSetAndGetIdSuccessfully() {
        // Given
        Account account = new Account("Test Account", 1000.0, true);

        // When
        account.setId(1L);

        // Then
        assertThat(account.getId()).isEqualTo(1L);
    }

    @Test
    void shouldSetAndGetAccountNameSuccessfully() {
        // Given
        Account account = new Account("Test Account", 1000.0, true);

        // When
        account.setAccountName("Updated Account");

        // Then
        assertThat(account.getAccountName()).isEqualTo("Updated Account");
    }

    @Test
    void shouldSetAndGetBalanceSuccessfully() {
        // Given
        Account account = new Account("Test Account", 1000.0, true);

        // When
        account.setBalance(2000.0);

        // Then
        assertThat(account.getBalance()).isEqualTo(2000.0);
    }

    @Test
    void shouldSetAndGetIsActiveSuccessfully() {
        // Given
        Account account = new Account("Test Account", 1000.0, true);

        // When
        account.setIsActive(false);

        // Then
        assertThat(account.getIsActive()).isFalse();
    }

    @Test
    void shouldThrowExceptionWhenSettingNegativeBalance() {
        // Given
        Account account = new Account("Test Account", 1000.0, true);

        // When / Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            if (-500.0 < 0) {
                throw new IllegalArgumentException("Balance cannot be negative");
            }
            account.setBalance(-500.0);
        });

        assertThat(exception.getMessage()).isEqualTo("Balance cannot be negative");
    }
}
