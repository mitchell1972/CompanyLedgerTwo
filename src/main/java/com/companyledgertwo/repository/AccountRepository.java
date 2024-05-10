package com.companyledgertwo.repository;

import com.companyledgertwo.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByAccountName(String name);
    List<Account> findByBalanceGreaterThan(Double minimumBalance);
    List<Account> findByBalanceLessThan(Double maximumBalance);
    List<Account> findByIsActive(Boolean isActive);
}
