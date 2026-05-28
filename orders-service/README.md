# Orders Service

Serviço de pedidos em uma arquitetura de microsserviços baseada no padrão **Saga** para coordenação de transações distribuídas.

## Arquitetura

O serviço utiliza o **Transactional Outbox Pattern**: ao criar um pedido, tanto o registro do pedido quanto um evento de outbox (`order.created`) são gravados no MySQL na mesma transação, garantindo entrega confiável de eventos para os consumidores downstream via Kafka.

### Estrutura do Projeto

```
orders-service/
├── cmd/api/          # Entrypoint — inicializa dependências e servidor Gin
├── internal/
│   ├── handler/      # HTTP handlers (binding de request, escrita de response)
│   ├── service/      # Lógica de negócio e orquestração
│   ├── repository/   # Persistência MySQL via database/sql
│   ├── domain/       # Modelos de domínio, enums e payloads de eventos
│   ├── consumer/     # Consumers Kafka para eventos de compensação da Saga
│   └── config/       # Configuração via variáveis de ambiente
├── INIT.sql          # Schema do banco (MySQL/InnoDB)
├── Dockerfile
└── go.mod
```

## Tecnologias

- **Go** (Gin HTTP framework)
- **MySQL** — persistência de pedidos e outbox
- **Redis** — reservado para verificações de idempotência
- **Kafka** — mensageria para eventos da Saga
- **OpenTelemetry** — tracing distribuído

## API

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/health` | Health check |
| POST | `/v1/orders` | Criar um pedido |

### Criar Pedido

```bash
curl -X POST http://localhost:8080/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "uuid",
    "productId": 1,
    "quantity": 2,
    "total": 99.90,
    "paymentType": "CREDIT_CARD"
  }'
```

## Configuração

| Variável | Default | Descrição |
|----------|---------|-----------|
| `SERVER_PORT` | `:8080` | Endereço HTTP |
| `MYSQL_DSN` | `root:root@tcp(localhost:3307)/orders?parseTime=true` | Connection string MySQL |
| `REDIS_ADDR` | `localhost:6379` | Endereço Redis |
| `REDIS_PASS` | (vazio) | Senha Redis |
| `KAFKA_INVENTORY_TOPIC` | `inventory.insufficient-stock` | Tópico Kafka para eventos de estoque insuficiente |

## Build & Run

```bash
# Build
go build -o orders-service ./cmd/api

# Run
go run ./cmd/api
```

## Database

O schema está em `INIT.sql`. Duas tabelas principais:

- **orders** — pedidos com status (`PENDING`, `CONFIRMED`, `CANCELED`)
- **outbox** — eventos pendentes para publicação via relay (`PENDING`, `PROCESSING`, `SENT`, `FAILED`, `DEAD_LETTER`)
