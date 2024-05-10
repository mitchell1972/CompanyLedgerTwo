package performance;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class AccountAndTransactionSimulation extends Simulation {

    private static final HttpProtocolBuilder httpProtocol = http
        .baseUrl("http://localhost:8080")
        .acceptHeader("application/json")
        .contentTypeHeader("application/json");

    private static final ScenarioBuilder createTransactionScenario = scenario("Create Account and Transaction Scenario")
        .exec(
            http("Create Account")
                .post("/api/accounts")
                .body(StringBody("{\"accountName\":\"Test Account\",\"balance\":1000.0,\"isActive\":true}"))
                .check(status().is(200))
                .check(jsonPath("$.id").saveAs("accountId"))
        )
        .pause(1)
        .exec(
            http("Create Transaction")
                .post("/api/accounts/#{accountId}/transactions")
                .body(StringBody("{\"date\":\"2024-05-09\",\"amount\":100.0}"))
                .check(status().is(200))
        );

    {
        setUp(createTransactionScenario.injectOpen(atOnceUsers(10))).protocols(httpProtocol);
    }
}
