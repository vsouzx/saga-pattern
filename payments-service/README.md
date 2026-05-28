# Payments Service

Microserviço de pagamentos em um sistema de processamento de pedidos baseado no padrão Saga. Consome eventos `inventory.reserved` do Kafka, processa pagamentos e produz eventos `payments.authorized` ou `payments.denied`.

Faz parte do sistema distribuído **orders-saga**.

## Stack

- **Go 1.25.8**
- **Gin** — HTTP server (`:8081`)
- **MySQL** — persistência
- **Kafka** (`segmentio/kafka-go`) — mensageria
- **OpenTelemetry** — tracing distribuído
- **Zap** — logging estruturado

## Executando

### Pré-requisitos

- Go 1.25+
- MySQL rodando em `localhost:3308`
- Kafka rodando em `localhost:29092`

### Build e execução

```bash
go build ./...           # Build
go run cmd/api/main.go   # Executa o serviço
```

### Testes

```bash
go test ./...
```

## Configuração

Todas as configurações são feitas via variáveis de ambiente:

| Variável | Default |
|---|---|
| `SERVER_PORT` | `:8081` |
| `MYSQL_DSN` | `root:root@tcp(localhost:3308)/payments?parseTime=true` |
| `KAFKA_BROKERS` | `localhost:29092` |
| `KAFKA_INVENTORY_RESERVED_TOPIC` | `inventory.reserved` |
| `KAFKA_PAYMENT_AUTHORIZED_TOPIC` | `payments.authorized` |
| `KAFKA_PAYMENT_DENIED_TOPIC` | `payments.denied` |
| `OUTBOX_BATCH_SIZE` | `10` |

## Tópicos Kafka

| Direção | Tópico | Descrição |
|---|---|---|
| Consume | `inventory.reserved` | Dispara o processamento de pagamento |
| Produz | `payments.authorized` | Pagamento autorizado — saga continua |
| Produz | `payments.denied` | Pagamento negado — compensação |

## Arquitetura

```
cmd/api/main.go          # Entrypoint
internal/
  config/                # Configuração, conexões (MySQL, Kafka), logger e tracer
  logger/                # Logger context-aware com trace/span IDs
```

O serviço utiliza o padrão **Transactional Outbox** para garantir consistência entre o banco de dados e a publicação de eventos no Kafka.

## Health Check

```
GET /health
```

## Endpoints

| Método | Path | Descrição |
|---|---|---|
| GET | `/health` | Health check |
