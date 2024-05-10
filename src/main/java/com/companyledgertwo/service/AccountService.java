package com.companyledgertwo.service;

import com.companyledgertwo.model.Account;
import com.companyledgertwo.model.Transaction;

import java.util.List;

public interface AccountService {

    Account createAccount(Account account);

    Account getAccount(Long accountId);

    Account findByAccountName(String name);

    Account updateBalance(Long accountId, Double newBalance);

    List<Account> findByBalanceGreaterThan(Double minimumBalance);

    List<Account> findByBalanceLessThan(Double maximumBalance);

    List<Account> findByIsActive(Boolean isActive);

    List<Transaction> listTransactionsForAccount(Long id);

    List<Account> listAllAccounts();
}
