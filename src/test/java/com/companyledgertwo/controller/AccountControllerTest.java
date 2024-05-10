package com.companyledgertwo.controller;

import com.companyledgertwo.model.Account;
import com.companyledgertwo.model.Transaction;
import com.companyledgertwo.service.AccountService;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private AccountController accountController;

    @MockBean
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.standaloneSetup(accountController);
    }


    @Test
    void testCreateAccount() {
        Account account = new Account();
        account.setAccountName("Test Account");

        when(accountService.createAccount(any(Account.class))).thenReturn(account);

        given()
                .contentType(ContentType.JSON)
                .body(account)
                .when()
                .post("/api/accounts")
                .then()
                .statusCode(OK.value())
                .contentType(ContentType.JSON)
                .body("accountName", equalTo("Test Account"));
    }

    @Test
    void testGetAccount() {
        Account account = new Account();
        account.setId(1L);
        account.setAccountName("Test Account");

        when(accountService.getAccount(1L)).thenReturn(account);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/accounts/1")
                .then()
                .statusCode(OK.value())
                .contentType(ContentType.JSON)
                .body("accountName", equalTo("Test Account"));
    }

    @Test
    void testGetAccountNotFound() {
        // Mock the accountService to return null for any account ID
        when(accountService.getAccount(anyLong())).thenReturn(null);

        // Send a GET request to the /api/accounts/{id} endpoint and verify the response
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/accounts/1")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.JSON)
                .body("error", equalTo("Account not found"));
    }


    @Test
    void testCreateTransactionAccountNotFound() {
        Transaction transaction = new Transaction();
        transaction.setAmount(100.0);
        transaction.setAccountId(1L);

        when(accountService.getAccount(anyLong())).thenReturn(null);

        given()
                .contentType(ContentType.JSON)
                .body(transaction)
                .when()
                .get("/api/accounts/1/transactions")
                .then()
                .statusCode(NOT_FOUND.value())
                .contentType(ContentType.JSON)
                .body("error", equalTo("Transactions not found for account id: 1"));
    }

    @Test
    void testListAllAccounts() {
        Account account = new Account();
        account.setAccountName("Test Account");
        List<Account> accounts = Collections.singletonList(account);

        when(accountService.listAllAccounts()).thenReturn(accounts);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/accounts")
                .then()
                .statusCode(OK.value())
                .contentType(ContentType.JSON)
                .body("[0].accountName", equalTo("Test Account"));
    }

    @Test
    void testGetTransactions() {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(100.0);
        transaction.setAccountId(1L);
        List<Transaction> transactions = Collections.singletonList(transaction);

        when(accountService.listTransactionsForAccount(1L)).thenReturn(transactions);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/accounts/1/transactions")
                .then()
                .statusCode(OK.value())
                .contentType(ContentType.JSON)
                .body("[0].amount", equalTo(100.0f));
    }

    @Test
    void testGetTransactionsAccountNotFound() {
        // Mock the accountService to return an empty list for any account ID
        when(accountService.listTransactionsForAccount(anyLong())).thenReturn(Collections.emptyList());

        // Send a GET request to the /api/accounts/{id}/transactions endpoint and verify the response
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/accounts/1/transactions")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.JSON)
                .body("error", equalTo("Transactions not found for account id: 1"));
    }
}
