# TiaaDeals Server

A Spring Boot-based e-commerce server API for the TiaaDeals platform.

## Overview

This project has been migrated from Node.js to Spring Boot to provide better performance, enterprise-grade features, and improved maintainability.

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring Security**
- **PostgreSQL**
- **JWT Authentication**
- **Swagger/OpenAPI**
- **Maven**

## Project Structure

```
src/
├── main/
│   ├── java/com/tiaadeals/server/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── entity/         # JPA entities
│   │   ├── exception/      # Custom exceptions
│   │   ├── repository/     # Spring Data repositories
│   │   ├── security/       # Security configuration
│   │   ├── service/        # Business logic
│   │   └── TiaaDealsApplication.java
│   └── resources/
│       ├── application.properties
│       └── application.yml
└── test/
    ├── java/com/tiaadeals/server/
    │   └── SimpleTest.java
    └── resources/
        └── application-test.properties
```

## Features

- **User Management**: Registration, authentication, profile management
- **Product Management**: CRUD operations, search, filtering
- **Cart Management**: Add/remove items, quantity management
- **Category Management**: Product categorization
- **Security**: JWT-based authentication and authorization
- **API Documentation**: Swagger/OpenAPI integration
- **Exception Handling**: Global exception handling
- **Validation**: Bean validation for data integrity

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL database

### Installation

1. Clone the repository
2. Configure database connection in `application.yml`
3. Run the application:

```bash
mvn spring-boot:run
```

### API Documentation

Once the application is running, you can access the Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

## Migration Summary

This project was successfully migrated from Node.js to Spring Boot with the following changes:

- **Package Structure**: Updated from `com.tiaadeals.backend` to `com.tiaadeals.server`
- **Architecture**: Migrated from Express.js to Spring Boot
- **Database Access**: From raw SQL to Spring Data JPA
- **Security**: Enhanced with Spring Security
- **Testing**: JUnit 5 with Mockito
- **Documentation**: Swagger/OpenAPI integration

## Development

### Building the Project

```bash
mvn clean compile
```

### Running Tests

```bash
mvn test
```

### Creating JAR

```bash
mvn clean package
```

## Docker Support

The project includes Docker configuration for containerized deployment.

## License

This project is licensed under the ISC License.
