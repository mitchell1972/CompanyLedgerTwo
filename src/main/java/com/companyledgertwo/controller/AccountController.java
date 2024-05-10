package com.companyledgertwo.controller;

import com.companyledgertwo.model.Account;
import com.companyledgertwo.model.Transaction;
import com.companyledgertwo.service.AccountService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);
    private final AccountService accountService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createAccount(@RequestBody Account account) {
        LOGGER.info("Request to create an account: {}", account);

        if (account.getAccountName() == null || account.getAccountName().isEmpty()) {
            String errorMessage = "Account name cannot be null or empty";
            LOGGER.error(errorMessage);
            return jsonErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
        }

        try {
            Account createdAccount = accountService.createAccount(account);
            return jsonSuccessResponse(createdAccount);
        } catch (RuntimeException e) {
            LOGGER.error("Internal server error while creating account: {}", e.getMessage(), e);
            return jsonErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        } catch (JsonProcessingException e) {
            LOGGER.error("Error processing JSON while creating account: {}", e.getMessage(), e);
            return jsonErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing JSON: " + e.getMessage());
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAccount(@PathVariable Long id) {
        LOGGER.info("Request to get account by id: {}", id);

        Account account = accountService.getAccount(id);
        if (account == null) {
            String errorMessage = "Account not found";
            LOGGER.error(errorMessage);
            return jsonErrorResponse(HttpStatus.NOT_FOUND, errorMessage);
        }

        try {
            return jsonSuccessResponse(account);
        } catch (JsonProcessingException e) {
            LOGGER.error("Error processing JSON while retrieving account: {}", e.getMessage(), e);
            return jsonErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing JSON: " + e.getMessage());
        }
    }

    @GetMapping(value = "/{id}/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getTransactions(@PathVariable Long id) {
        LOGGER.info("Request to get transactions for account id: {}", id);

        List<Transaction> transactions = accountService.listTransactionsForAccount(id);
        if (transactions == null || transactions.isEmpty()) {
            String errorMessage = "Transactions not found for account id: " + id;
            LOGGER.error(errorMessage);
            return jsonErrorResponse(HttpStatus.NOT_FOUND, errorMessage);
        }

        try {
            return jsonSuccessResponse(transactions);
        } catch (JsonProcessingException e) {
            String errorMessage = "Error processing JSON: " + e.getMessage();
            LOGGER.error(errorMessage, e);
            return jsonErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> listAllAccounts() {
        LOGGER.info("Request to list all accounts");

        List<Account> accounts = accountService.listAllAccounts();
        try {
            return jsonSuccessResponse(accounts);
        } catch (JsonProcessingException e) {
            String errorMessage = "Error processing JSON: " + e.getMessage();
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
}
