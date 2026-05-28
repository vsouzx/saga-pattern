# Inventory Service

Microservice responsible for managing products, stock, and stock reservations as part of a distributed orders saga (choreography pattern).

## Tech Stack

- **Kotlin** 2.1.10 on **Java 21**
- **Spring Boot** 3.4.5 (Web MVC, Data JPA, Kafka)
- **PostgreSQL** for persistence
- **Apache Kafka** for event-driven communication
- **OpenTelemetry** for distributed tracing

## Architecture

Hexagonal architecture with clear separation between domain, application (use cases/ports), and adapter layers.

```
src/main/kotlin/br/com/souza/inventory_service/
├── adapter/
│   ├── in/
│   │   ├── consumer/       # Kafka consumers (order, payments)
│   │   └── web/            # REST controllers
│   └── out/                # JPA repositories, outbox relay
├── application/
│   ├── domain/
│   │   ├── model/          # Domain entities and commands
│   │   └── service/        # Use case implementations
│   └── ports/
│       ├── in/             # Input ports (use cases)
│       └── out/            # Output ports (repositories)
└── infrastructure/
    ├── kafka/              # Kafka consumer/producer config
    └── observability/      # OpenTelemetry config
```

## Saga Participation

This service participates in the orders saga by consuming and producing Kafka events:

| Topic | Direction | Action |
|-------|-----------|--------|
| `orders.created` | Consume | Reserves stock for the order |
| `orders.confirmed` | Consume | Confirms the reservation (final happy path step) |
| `payments.denied` | Consume | Releases reserved stock (compensation) |
| `inventory.reserved` | Produce | Notifies that stock was reserved successfully |
| `inventory.insufficient-stock` | Produce | Notifies that reservation failed |
| `inventory.released` | Produce | Notifies that stock was released |

## Running

### Prerequisites

- Java 21+
- PostgreSQL
- Apache Kafka

### Build

```bash
./mvnw package
```

### Run tests

```bash
./mvnw test
```

### Run the application

```bash
./mvnw spring-boot:run
```

### Database setup

Apply the schema from `INIT.sql` to your PostgreSQL database before starting the service.

## API Endpoints

- `POST /products` — Create a product
- `GET /products` — List all products
- `POST /products/{id}/stock` — Create stock for a product
- `GET /products/{id}/stock` — List stock for a product
- `PATCH /products/{id}/stock/quantity` — Update stock quantity
