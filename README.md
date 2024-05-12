# CompanyLedgerTwo README

## Overview
CompanyLedgerTwo is an application for financial management created using Spring Boot. It utilizes PostgreSQL as the persistence layer, offering full CRUD capabilities for managing accounts and transactions.

## Requirements
- **Java JDK 11+**
- **Maven 3.6.0+**
- **PostgreSQL 12+**

## Quick Setup
1. **Database Setup**:
    - Create and configure `mydatabase` on your local machine with the necessary tables.
    - When the tests are run, the required tables are automatically created.

2. **Configuration**:
    - Update `application.properties` and `application-test.properties` to match your PostgreSQL settings.

## Usage
- **Run Application**: Execute `mvn spring-boot:run` to start the application.
- **Unit Tests**: Run `mvn test` to execute the unit tests.
- **Integration Tests**: Execute `mvn verify` to perform integration tests.
- **Performance Tests**: Run `mvn gatling:test` or `mvn verify` for performance testing.
- **API Access**: Access the Swagger UI at [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) to interact with the API.

## Support
For support, please refer to the project repository or contact the project team.
