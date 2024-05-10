package com.companyledgertwo.controller;

import com.companyledgertwo.model.Transaction;
import com.companyledgertwo.service.TransactionService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.mockito.ArgumentMatchers.*;

public class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private Transaction transaction1;
    private Transaction transaction2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        transaction1 = new Transaction(1L, LocalDate.now(), 100.0);
        transaction1.setId(1L);

        transaction2 = new Transaction(1L, LocalDate.now().minusDays(1), 200.0);
        transaction2.setId(2L);

        RestAssuredMockMvc.standaloneSetup(transactionController);
    }

    @Test
    void shouldCreateTransaction() {
        BDDMockito.given(transactionService.createTransaction(any(Transaction.class))).willReturn(transaction1);

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{\"accountId\":1,\"date\":\"" + LocalDate.now() + "\",\"amount\":100.0}")
                .when()
                .post("/transactions")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.equalTo(1))
                .body("accountId", Matchers.equalTo(1))
                .body("date", Matchers.equalTo(LocalDate.now().toString()))
                .body("amount", Matchers.equalTo(100.0F));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingInvalidTransaction() {
        BDDMockito.given(transactionService.createTransaction(any(Transaction.class))).willThrow(new IllegalArgumentException());

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{\"accountId\":null,\"date\":null,\"amount\":-100.0}")
                .when()
                .post("/transactions")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldGetTransactionsByAccountId() {
        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);
        BDDMockito.given(transactionService.findTransactionsByAccountId(anyLong())).willReturn(transactions);

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/transactions/account/1")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("[0].accountId", Matchers.equalTo(1))
                .body("[0].amount", Matchers.equalTo(100.0F))
                .body("[1].accountId", Matchers.equalTo(1))
                .body("[1].amount", Matchers.equalTo(200.0F));
    }

    @Test
    void shouldReturnEmptyListWhenAccountNotFound() {
        BDDMockito.given(transactionService.findTransactionsByAccountId(anyLong())).willReturn(Collections.emptyList());

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/transactions/account/99")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("isEmpty()", Matchers.equalTo(true));
    }

    @Test
    void shouldGetTransactionsBetweenDates() {
        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);
        BDDMockito.given(transactionService.findTransactionsBetweenDates(any(LocalDate.class), any(LocalDate.class))).willReturn(transactions);

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("start", LocalDate.now().minusDays(1).toString())
                .param("end", LocalDate.now().toString())
                .when()
                .get("/transactions/between")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("[0].accountId", Matchers.equalTo(1))
                .body("[0].amount", Matchers.equalTo(100.0F))
                .body("[1].accountId", Matchers.equalTo(1))
                .body("[1].amount", Matchers.equalTo(200.0F));
    }

    @Test
    void shouldReturnEmptyListWhenNoTransactionsBetweenDates() {
        BDDMockito.given(transactionService.findTransactionsBetweenDates(any(LocalDate.class), any(LocalDate.class))).willReturn(Collections.emptyList());

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("start", LocalDate.now().minusDays(1).toString())
                .param("end", LocalDate.now().toString())
                .when()
                .get("/transactions/between")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("isEmpty()", Matchers.equalTo(true));
    }

    @Test
    void shouldGetTransactionsGreaterThan() {
        List<Transaction> transactions = Arrays.asList(transaction2);
        BDDMockito.given(transactionService.findTransactionsGreaterThan(anyDouble())).willReturn(transactions);

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("amount", 150.0)
                .when()
                .get("/transactions/greaterThan")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("[0].accountId", Matchers.equalTo(1))
                .body("[0].amount", Matchers.equalTo(200.0F));
    }

    @Test
    void shouldReturnEmptyListWhenNoTransactionsGreaterThan() {
        BDDMockito.given(transactionService.findTransactionsGreaterThan(anyDouble())).willReturn(Collections.emptyList());

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("amount", 300.0)
                .when()
                .get("/transactions/greaterThan")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("isEmpty()", Matchers.equalTo(true));
    }

    @Test
    void shouldGetTransactionsLessThan() {
        List<Transaction> transactions = Arrays.asList(transaction1);
        BDDMockito.given(transactionService.findTransactionsLessThan(anyDouble())).willReturn(transactions);

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("amount", 150.0)
                .when()
                .get("/transactions/lessThan")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("[0].accountId", Matchers.equalTo(1))
                .body("[0].amount", Matchers.equalTo(100.0F));
    }

    @Test
    void shouldReturnEmptyListWhenNoTransactionsLessThan() {
        BDDMockito.given(transactionService.findTransactionsLessThan(anyDouble())).willReturn(Collections.emptyList());

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("amount", 50.0)
                .when()
                .get("/transactions/lessThan")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("isEmpty()", Matchers.equalTo(true));
    }
}
