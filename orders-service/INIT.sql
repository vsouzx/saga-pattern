
CREATE DATABASE IF NOT EXISTS orders;

USE orders;

-- ------------------------------------------------------------
-- ORDERS
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS orders (
    id               CHAR(36)     NOT NULL,
    user_id          CHAR(36)     NOT NULL,
    product_id       INT     NOT NULL,
    quantity         INT          NOT NULL,
    payment_type     VARCHAR(20)  NOT NULL,
    status           VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    idempotency_key  VARCHAR(255) NOT NULL,
    reason           TEXT         NULL                         COMMENT 'Motivo de cancelamento, se houver',
    created_at       DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at       DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),

    PRIMARY KEY (id),

    -- Garante unicidade no nível do banco, mesmo que Redis falhe
    CONSTRAINT uq_orders_idempotency_key UNIQUE (idempotency_key),

    -- Validações básicas
    CONSTRAINT chk_orders_quantity   CHECK (quantity > 0),
    CONSTRAINT chk_orders_status     CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELED'))
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Consultas por usuário
CREATE INDEX idx_orders_user_id ON orders (user_id);

-- Relay e dashboards filtram por status
CREATE INDEX idx_orders_status ON orders (status);


-- ------------------------------------------------------------
-- OUTBOX (Transactional Outbox Pattern)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS outbox (
    id              CHAR(36)     NOT NULL,
    aggregate_type  VARCHAR(50)  NOT NULL                      COMMENT 'Ex: ORDER',
    aggregate_id    CHAR(36)     NOT NULL                      COMMENT 'Ex: order_id',
    event_type      VARCHAR(100) NOT NULL                      COMMENT 'Ex: order.created, order.confirmed, order.cancelled',
    payload         JSON         NOT NULL                      COMMENT 'Corpo completo do evento para o Kafka',
    trace_parent    VARCHAR(255) NOT NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    retries_count   INT          NOT NULL DEFAULT 0,
    max_retries     INT          NOT NULL DEFAULT 5,
    created_at      DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    sent_at         DATETIME(3)  NULL,
    locked_at       DATETIME(3)  NULL,

    PRIMARY KEY (id),

    CONSTRAINT chk_outbox_status CHECK (status IN ('PENDING', 'PROCESSING', 'SENT', 'FAILED', 'DEAD_LETTER'))
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Índice composto para o relay: busca eventos pendentes/falhados em ordem de criação
-- Cobre a query: WHERE status IN ('PENDING','FAILED')
-- ORDER BY created_at ASC LIMIT N FOR UPDATE SKIP LOCKED
CREATE INDEX idx_outbox_relay ON outbox (status, created_at);

-- Consultar eventos por aggregate (debug, auditoria)
CREATE INDEX idx_outbox_aggregate ON outbox (aggregate_type, aggregate_id);