package com.companyledgertwo.service;

import com.companyledgertwo.model.Account;
import com.companyledgertwo.model.Transaction;
import com.companyledgertwo.repository.AccountRepository;
import com.companyledgertwo.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public Account getAccount(Long accountId) {
        return accountRepository.findById(accountId).orElse(null);
    }

    @Override
    public Account findByAccountName(String name) {
        return accountRepository.findByAccountName(name);
    }

    @Override
    public Account updateBalance(Long accountId, Double newBalance) {
        Account account = getAccount(accountId);
        if (account != null) {
            account.setBalance(newBalance);
            return accountRepository.save(account);
        }
        return null;
    }

    @Override
    public List<Account> findByBalanceGreaterThan(Double minimumBalance) {
        return accountRepository.findByBalanceGreaterThan(minimumBalance);
    }

    @Override
    public List<Account> findByBalanceLessThan(Double maximumBalance) {
        return accountRepository.findByBalanceLessThan(maximumBalance);
    }

    @Override
    public List<Account> findByIsActive(Boolean isActive) {
        return accountRepository.findByIsActive(isActive);
    }

    @Override
    public List<Transaction> listTransactionsForAccount(Long accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    @Override
    public List<Account> listAllAccounts() {
        return List.of();
    }
}
