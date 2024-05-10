package com.companyledgertwo;

import com.companyledgertwo.model.Account;
import com.companyledgertwo.model.Transaction;
import com.companyledgertwo.repository.AccountRepository;
import com.companyledgertwo.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AccountControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
    }

    private void assertErrorResponse(ResponseEntity<String> response, HttpStatus expectedStatus, String expectedMessage) {
        assertEquals(expectedStatus, response.getStatusCode(), "Expected HTTP status " + expectedStatus + " but got " + response.getStatusCode());
        String responseBody = response.getBody();
        assertNotNull(responseBody, "Response body is null");
        assertTrue(responseBody.contains(expectedMessage), "Expected error message not found in response body: " + responseBody);
    }

    @Test
    void testCreateAccount() {
        Account account = new Account("Test Account", 1000.0, true);
        ResponseEntity<Account> response = restTemplate.postForEntity("/api/accounts", account, Account.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Account createdAccount = response.getBody();
        assertNotNull(createdAccount);
        assertTrue(createdAccount.getId() > 0);
        assertEquals("Test Account", createdAccount.getAccountName());
    }

    @Test
    void testGetExistingAccount() {
        Account account = new Account("Test Account", 1000.0, true);
        account = accountRepository.save(account);

        ResponseEntity<Account> response = restTemplate.getForEntity("/api/accounts/" + account.getId(), Account.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Account retrievedAccount = response.getBody();
        assertNotNull(retrievedAccount);
        assertEquals(account.getId(), retrievedAccount.getId());
        assertEquals("Test Account", retrievedAccount.getAccountName());
    }

    @Test
    void testGetNonExistingAccount() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/accounts/999", String.class);
        assertErrorResponse(response, HttpStatus.NOT_FOUND, "Account not found");
    }

    @Test
    void testGetTransactionsForExistingAccount() {
        Account account = new Account("Test Account", 1000.0, true);
        account = accountRepository.save(account);

        Transaction transaction1 = new Transaction(account.getId(), LocalDate.now(), 100.0);
        Transaction transaction2 = new Transaction(account.getId(), LocalDate.now().minusDays(1), 200.0);
        transactionRepository.saveAll(List.of(transaction1, transaction2));

        ResponseEntity<String> response = restTemplate.getForEntity("/api/accounts/" + account.getId() + "/transactions", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Transaction[] transactions = deserializeTransactions(response.getBody());
        assertNotNull(transactions);
        assertEquals(2, transactions.length);
    }

    @Test
    void testGetTransactionsForNonExistingAccount() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/accounts/999/transactions", String.class);
        assertErrorResponse(response, HttpStatus.NOT_FOUND, "Transactions not found for account id: 999");
    }

    private Transaction deserializeTransaction(String json) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Register the JavaTimeModule

        try {
            return mapper.readValue(json, Transaction.class);
        } catch (IOException e) {
            fail("Failed to deserialize Transaction object: " + e.getMessage());
            return null;
        }
    }

    private Transaction[] deserializeTransactions(String json) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Register the JavaTimeModule

        try {
            return mapper.readValue(json, Transaction[].class);
        } catch (IOException e) {
            fail("Failed to deserialize Transaction array object: " + e.getMessage());
            return new Transaction[0];
        }
    }
}
