package performance;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.LocalDate;

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
                            .post("/transactions")
                            .body(StringBody("{\"accountId\":1,\"date\":\"" + LocalDate.now() + "\",\"amount\":100.0}"))
                            .check(status().is(200))
            );

    {
        setUp(
                createTransactionScenario.injectOpen(
                        atOnceUsers(15),
                        rampUsers(85).during(3 * 60)
                )
        ).protocols(httpProtocol);
    }
}
