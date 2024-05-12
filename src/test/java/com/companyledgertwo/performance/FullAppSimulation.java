package com.companyledgertwo.performance;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.LocalDate;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class FullAppSimulation extends Simulation {


    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080") // Base URL
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    ScenarioBuilder scn = scenario("Full Application Stress Test")
            // Create Account
            .exec(http("Create Account")
                    .post("/api/accounts")
                    .body(StringBody("{\n" +
                            "  \"accountName\" : \"Test Account\",\n" +
                            "  \"balance\" : 1000.0,\n" +
                            "  \"isActive\" : true\n" +
                            "}"))
                    .asJson()
                    .check(status().is(200))
                    .check(jsonPath("$.id").saveAs("accountId")))
            .pause(1)
            // Get Account
            .exec(http("Get Account")
                    .get("/api/accounts/1")
                    .body(StringBody("{\"accountName\":\"Performance Test Account\"}"))
                    .asJson()
                    .check(status().is(200)))
            .pause(1)
            // List All Accounts
            .exec(http("List All Accounts")
                    .get("/api/accounts")
                    .check(status().is(200)))
            .pause(1)
            // Create Transaction
            .exec(http("Create Transaction")
                    .post("/transactions")
                    .body(StringBody("{\"accountId\":1,\"date\":\"" + LocalDate.now() + "\",\"amount\":100.50}"))
                    .asJson()
                    .check(status().is(200)))
            .pause(1)
            // Get Transactions for Account
            .exec(http("Get Transactions for Account")
                    .get("/transactions/account/1")
                    .check(status().is(200)))
            .pause(1)
            // Get Transactions Greater Than
            .exec(http("Transactions Greater Than $100")
                    .get("/transactions/greaterThan?amount=100.50")
                    .check(status().is(200)))
            .pause(1)
            // Get Transactions Less Than
            .exec(http("Transactions Less Than $500")
                    .get("/transactions/lessThan?amount=499.99")
                    .check(status().is(200)))
            .pause(1)
            // Get Transactions Between Dates
            .exec(http("Transactions Between Dates")
                    .get("/transactions/between?start=2024-05-01&end=2024-05-31")
                    .check(status().is(200)));

    {
        setUp(scn.injectOpen(
                rampUsers(10).during(60), // Simulate ramping up to 100 users over 180 seconds
                nothingFor(10), // Pause for 10 seconds
                constantUsersPerSec(2).during(60) // Constant load of 15 users per second
        )).protocols(httpProtocol);
    }
}
