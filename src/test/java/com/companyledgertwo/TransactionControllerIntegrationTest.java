package com.companyledgertwo;

import com.companyledgertwo.model.Transaction;
import com.companyledgertwo.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class TransactionControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
    }

    // Helper method to assert an error response
    private void assertErrorResponse(ResponseEntity<String> response, HttpStatus expectedStatus, String expectedMessage) {
        assertEquals(expectedStatus, response.getStatusCode(), "Expected HTTP status " + expectedStatus + " but got " + response.getStatusCode());
        String responseBody = response.getBody();
        assertNotNull(responseBody, "Response body is null");
        assertTrue(responseBody.contains(expectedMessage), "Expected error message not found in response body: " + responseBody);
    }

    // Helper method to create and verify a valid transaction
    private void createAndVerifyTransaction(Transaction transaction) {
        ResponseEntity<Transaction> response = restTemplate.postForEntity("/transactions", transaction, Transaction.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).getId() > 0);
    }

    @Test
    void testCreateTransaction() {
        createAndVerifyTransaction(new Transaction(1L, LocalDate.now(), 100.0));
    }

    @Test
    void testCreateTransactionWithInvalidAccountId() {
        Transaction transaction = new Transaction(null, LocalDate.now(), 100.0);
        ResponseEntity<String> response = restTemplate.postForEntity("/transactions", transaction, String.class);
        assertErrorResponse(response, HttpStatus.BAD_REQUEST, "Account ID cannot be null");
    }

    @Test
    void testCreateTransactionWithNullDate() {
        Transaction transaction = new Transaction(1L, null, 100.0);
        ResponseEntity<String> response = restTemplate.postForEntity("/transactions", transaction, String.class);
        assertErrorResponse(response, HttpStatus.BAD_REQUEST, "Invalid transaction data");
    }

    @Test
    void testCreateTransactionWithNegativeAmount() {
        Transaction transaction = new Transaction(1L, LocalDate.now(), -100.0);
        ResponseEntity<String> response = restTemplate.postForEntity("/transactions", transaction, String.class);
        assertErrorResponse(response, HttpStatus.BAD_REQUEST, "Invalid transaction data");
    }

    @Test
    void testGetTransactionsByAccountId() {
        List<Transaction> transactions = List.of(
                new Transaction(1L, LocalDate.now(), 100.0),
                new Transaction(1L, LocalDate.now().minusDays(1), 200.0)
        );
        transactionRepository.saveAll(transactions);

        ResponseEntity<Transaction[]> response = restTemplate.getForEntity("/transactions/account/1", Transaction[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Transaction[] retrievedTransactions = response.getBody();
        assertNotNull(retrievedTransactions);
        assertEquals(2, retrievedTransactions.length);
    }

    @Test
    void testGetTransactionsByNonExistingAccountId() {
        ResponseEntity<Transaction[]> response = restTemplate.getForEntity("/transactions/account/999", Transaction[].class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        Transaction[] transactions = response.getBody();
        assertNotNull(transactions);
        assertEquals(0, transactions.length);
    }

    @Test
    void testGetTransactionsBetweenDates() {
        List<Transaction> transactions = List.of(
                new Transaction(1L, LocalDate.of(2024, 5, 1), 100.0),
                new Transaction(1L, LocalDate.of(2024, 5, 2), 200.0),
                new Transaction(1L, LocalDate.of(2024, 5, 3), 300.0)
        );
        transactionRepository.saveAll(transactions);

        ResponseEntity<Transaction[]> response = restTemplate.getForEntity(
                "/transactions/between?start=2024-05-01&end=2024-05-03",
                Transaction[].class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Transaction[] retrievedTransactions = response.getBody();
        assertNotNull(retrievedTransactions);
        assertEquals(3, retrievedTransactions.length);
    }

    @Test
    void testGetTransactionsBetweenInvalidDates() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/transactions/between?start=invalid-date&end=2024-05-03",
                String.class
        );
        assertErrorResponse(response, HttpStatus.BAD_REQUEST, "Failed to convert");
    }

    @Test
    void testGetTransactionsGreaterThanAmount() {
        List<Transaction> transactions = List.of(
                new Transaction(1L, LocalDate.now(), 100.0),
                new Transaction(1L, LocalDate.now(), 200.0),
                new Transaction(1L, LocalDate.now(), 300.0)
        );
        transactionRepository.saveAll(transactions);

        ResponseEntity<Transaction[]> response = restTemplate.getForEntity("/transactions/greaterThan?amount=150.0", Transaction[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Transaction[] retrievedTransactions = response.getBody();
        assertNotNull(retrievedTransactions);
        assertEquals(2, retrievedTransactions.length);
    }

    @Test
    void testGetTransactionsGreaterThanWithNonNumericAmount() {
        ResponseEntity<String> response = restTemplate.getForEntity("/transactions/greaterThan?amount=non-numeric", String.class);
        assertErrorResponse(response, HttpStatus.BAD_REQUEST, "Failed to convert");
    }

    @Test
    void testGetTransactionsLessThanAmount() {
        List<Transaction> transactions = List.of(
                new Transaction(1L, LocalDate.now(), 100.0),
                new Transaction(1L, LocalDate.now(), 200.0),
                new Transaction(1L, LocalDate.now(), 300.0)
        );
        transactionRepository.saveAll(transactions);

        ResponseEntity<Transaction[]> response = restTemplate.getForEntity("/transactions/lessThan?amount=250.0", Transaction[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Transaction[] retrievedTransactions = response.getBody();
        assertNotNull(retrievedTransactions);
        assertEquals(2, retrievedTransactions.length);
    }

    @Test
    void testGetTransactionsLessThanWithNonNumericAmount() {
        ResponseEntity<String> response = restTemplate.getForEntity("/transactions/lessThan?amount=non-numeric", String.class);
        assertErrorResponse(response, HttpStatus.BAD_REQUEST, "Failed to convert");
    }
}
