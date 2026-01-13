# Notifications Service

This is a Spring Boot application designed to handle notifications across different channels based on user subscriptions.

## Design & Architecture

### Design Patterns
- **Strategy Pattern**: Implemented via the `NotificationStrategy` interface and its concrete implementations (`EmailStrategy`, `SmsStrategy`, `PushNotificationStrategy`). This allows the application to dynamically select the appropriate notification algorithm based on the user's subscribed channels.

### SOLID Principles
- **Single Responsibility Principle (SRP)**: Each notification strategy is responsible for a single channel type, keeping the logic isolated and maintainable.
- **Open/Closed Principle (OCP)**: The system is designed to be easily extensible. New notification channels can be added by creating new strategy classes without modifying the core service logic.
- **Dependency Inversion Principle (DIP)**: The `NotificationService` depends on the `NotificationStrategy` abstraction rather than concrete implementations, facilitating easier testing and loose coupling.

## Prerequisites

- **Java 21**: This project requires Java 21. Verify your installation with:
  ```bash
  java -version
  ```

## Setup and Execution

This project includes the Gradle Wrapper, so a manual Gradle installation is not required.

### 1. Build the Project

Compile the code and download dependencies:

```bash
./gradlew build
```

### 2. Run Tests

Execute the JUnit test suite:

```bash
./gradlew test
```

### 3. Run the Application

Start the Spring Boot application:

```bash
./gradlew bootRun
```
