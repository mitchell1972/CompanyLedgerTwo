package com.companyledgertwo.service;

import com.companyledgertwo.model.Account;
import com.companyledgertwo.model.Transaction;
import com.companyledgertwo.repository.AccountRepository;
import com.companyledgertwo.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account account;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        account = new Account("Test Account", 1000.0, true);
        account.setId(1L);

        transaction = new Transaction(1L, LocalDate.now(), 100.0);
        transaction.setId(1L);
    }

    @Test
    void shouldCreateAccount() {
        given(accountRepository.save(any(Account.class))).willReturn(account);
        Account createdAccount = accountService.createAccount(new Account("New Account", 500.0, true));

        assertEquals(account.getId(), createdAccount.getId());
        assertEquals(account.getAccountName(), createdAccount.getAccountName());
    }

    @Test
    void shouldGetAccount() {
        given(accountRepository.findById(anyLong())).willReturn(Optional.of(account));
        Account foundAccount = accountService.getAccount(1L);

        assertEquals(account.getId(), foundAccount.getId());
    }

    @Test
    void shouldFindAccountByName() {
        given(accountRepository.findByAccountName("Test Account")).willReturn(account);
        Account foundAccount = accountService.findByAccountName("Test Account");

        assertEquals(account.getId(), foundAccount.getId());
        assertEquals("Test Account", foundAccount.getAccountName());
    }

    @Test
    void shouldFindAccountsByBalanceGreaterThan() {
        List<Account> accounts = Arrays.asList(
                new Account("Account 1", 2000.0, true),
                new Account("Account 2", 1500.0, true)
        );
        given(accountRepository.findByBalanceGreaterThan(anyDouble())).willReturn(accounts);
        List<Account> result = accountService.findByBalanceGreaterThan(1000.0);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(a -> a.getAccountName().equals("Account 1")));
    }

    @Test
    void shouldFindAccountsByBalanceLessThan() {
        List<Account> accounts = Collections.singletonList(new Account("Account 1", 500.0, true));
        given(accountRepository.findByBalanceLessThan(anyDouble())).willReturn(accounts);
        List<Account> result = accountService.findByBalanceLessThan(1000.0);

        assertEquals(1, result.size());
        assertEquals("Account 1", result.get(0).getAccountName());
    }

    @Test
    void shouldFindAccountsByIsActive() {
        List<Account> accounts = Arrays.asList(
                new Account("Account 1", 2000.0, true),
                new Account("Account 2", 1500.0, true)
        );
        given(accountRepository.findByIsActive(true)).willReturn(accounts);
        List<Account> result = accountService.findByIsActive(true);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(Account::getIsActive));
    }






}
