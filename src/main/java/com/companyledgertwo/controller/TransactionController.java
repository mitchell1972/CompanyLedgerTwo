package com.companyledgertwo.controller;

import com.companyledgertwo.model.Transaction;
import com.companyledgertwo.service.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionService transactionService;
    private final ObjectMapper objectMapper;

    public TransactionController(TransactionService transactionService, ObjectMapper objectMapper) {
        this.transactionService = transactionService;
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        //this.objectMapper = objectMapper;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createTransaction(@RequestBody Transaction transaction) {
        LOGGER.info("Request to create transaction: {}", transaction);

        if (transaction.getAccountId() == null) {
            String errorMessage = "Account ID cannot be null";
            LOGGER.error(errorMessage);
            return jsonErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
        }

        if (transaction.getAmount() <= 0 || transaction.getDate() == null) {
            String errorMessage = "Invalid transaction data";
            LOGGER.error(errorMessage);
            return jsonErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
        }

        try {
            Transaction createdTransaction = transactionService.createTransaction(transaction);
            return jsonSuccessResponse(createdTransaction);
        } catch (Exception e) {
            String errorMessage = "Internal server error: " + e.getMessage();
            LOGGER.error(errorMessage, e);
            return jsonErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
        }
    }

    private <T> ResponseEntity<String> jsonSuccessResponse(T object) throws JsonProcessingException {
        String jsonResponse = objectMapper.writeValueAsString(object);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonResponse);
    }

    private ResponseEntity<String> jsonErrorResponse(HttpStatus status, String errorMessage) {
        try {
            String errorJson = objectMapper.writeValueAsString(Collections.singletonMap("error", errorMessage));
            return ResponseEntity.status(status)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorJson);
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to convert error message to JSON", e);
            return ResponseEntity.status(status)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\":\"Internal server error\"}");
        }
    }

    @GetMapping(value = "/account/{accountId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Transaction>> getTransactionsByAccountId(@PathVariable Long accountId) {
        List<Transaction> transactions = transactionService.findTransactionsByAccountId(accountId);
        if (transactions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(transactions);
        }
        return ResponseEntity.ok(transactions);
    }

    @GetMapping(value = "/between", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Transaction>> getTransactionsBetweenDates(
            @RequestParam LocalDate start, @RequestParam LocalDate end) {
        List<Transaction> transactions = transactionService.findTransactionsBetweenDates(start, end);
        if (transactions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(transactions);
        }
        return ResponseEntity.ok(transactions);
    }

    @GetMapping(value = "/greaterThan", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Transaction>> getTransactionsGreaterThan(@RequestParam double amount) {
        List<Transaction> transactions = transactionService.findTransactionsGreaterThan(amount);
        if (transactions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(transactions);
        }
        return ResponseEntity.ok(transactions);
    }

    @GetMapping(value = "/lessThan", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Transaction>> getTransactionsLessThan(@RequestParam double amount) {
        List<Transaction> transactions = transactionService.findTransactionsLessThan(amount);
        if (transactions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(transactions);
        }
        return ResponseEntity.ok(transactions);
    }
}
