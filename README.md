# Eraport Application

## Overview

Eraport is a Spring Boot application designed to manage reporting and data. It leverages a modern Java tech stack and follows Domain-Driven Design (DDD) principles to ensure modularity, maintainability, and scalability.

## Technology Stack

### Core Frameworks & Languages

- **Java 21**: The application is built using the latest LTS version of Java, utilizing modern language features.
- **Spring Boot 3.2.0**: The core framework for bootstrapping and configuring the application.
- **Maven**: Build automation and dependency management tool.

### Database & Persistence

- **PostgreSQL**: The primary relational database management system.
- **Spring Data JPA**: Abstraction layer for data access, leveraging Hibernate for ORM.
- **Lombok**: Reduces boilerplate code (getters, setters, constructors) in entities and DTOs.

### Security

- **Spring Security**: Comprehensive security framework for authentication and authorization.
- **JWT (JSON Web Tokens)**: Used for stateless authentication (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`).

### Documentation

- **SpringDoc OpenAPI (Swagger)**: Automatically generates API documentation and provides a UI for testing endpoints.

### Monitoring

- **Spring Boot Actuator**: Provides production-ready features to help monitor and manage the application.
- **Micrometer Prometheus**: Exposes metrics in a format that can be scraped by a Prometheus server.

## Architecture

The project follows a **Layered Architecture** inspired by **Domain-Driven Design (DDD)**. This structure separates concerns into distinct layers, ensuring that the core business logic remains independent of external frameworks and UI concerns.

### Project Structure (`src/main/java/com/eraport`)

The project adopts a **Layered Architecture with Feature Grouping**, organizing files by feature _within_ each layer to improve navigability and modularity.

#### 1. Domain Layer (`com.eraport.domain`)

The heart of the application, containing business logic and rules.

- **`entities`**: Persistent domain objects (e.g., `users/User.java`).
- **`repositories`**: Data access interfaces (e.g., `users/UserRepository.java`).
- **`services`**: Domain service interfaces and implementations (e.g., `users/UserQueryService.java`).

#### 2. Application Layer (`com.eraport.application`)

Orchestrates data flow between UI and Domain.

- **`services`**: Application service interfaces (e.g., `auth/AuthService.java`).
- **`dto`**: Data Transfer Objects (e.g., `auth/LoginRequest.java`).

#### 3. Infrastructure Layer (`com.eraport.infrastructure`)

Handles external communication and technical concerns.

- **`controllers`**: REST API endpoints (e.g., `auth/AuthController.java`).
- **`config`**: Spring configuration classes.
- **`security`**: Security configurations.

#### 4. Shared Layer in Application/Infrastructure

Common utilities are grouped under `shared` packages within layers (e.g., `application/dto/shared/ApiResponse.java`).

## Key Design Patterns

- **Layered Architecture with Feature Grouping**: Files are organized by technical layer (`domain`, `infrastructure`) and then grouped by feature (`users`, `auth`) for better maintainability.
- **Service Interface Pattern**: All services are defined by interfaces (e.g., `UserQueryService`) with separate implementations (e.g., `UserQueryServiceImpl`) to promote loose coupling and easier testing.
- **CQRS (Command-Query Responsibility Segregation)**:
  - **Query Service**: Handles READ operations (e.g., `UserQueryService`).
  - **Command Service**: Handles WRITE operations (e.g., `UserCommandService`).
- **Database Read/Write Splitting**:
  - Dynamic routing of database connections using `AbstractRoutingDataSource`.
  - Transactions marked `@Transactional(readOnly = true)` route to the **Reader** datasource.
  - Transactions marked `@Transactional` (write) route to the **Writer** datasource.
- **Repository Pattern**: Spring Data JPA repositories for data access.
- **DTO Pattern**: explicit request/response objects for APIs.
- **Dependency Injection**: Spring IoC container management.

## Getting Started

1.  **Prerequisites**: Java 21, Maven 3.8+, PostgreSQL.
2.  **Configuration**: logical database connection details in `src/main/resources/application.properties` (or `.yaml`).
3.  **Build**: `mvn clean install`
4.  **Run**: `mvn spring-boot:run`

## Monitoring Endpoints

The application exposes the following endpoints for monitoring:

- **Health Check**: `http://localhost:8080/actuator/health` provides basic application health status.
- **Info**: `http://localhost:8080/actuator/info` displays arbitrary application info.
- **Prometheus Metrics**: `http://localhost:8080/actuator/prometheus` exposes metrics for Prometheus scraping.
  use
