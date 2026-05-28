CREATE DATABASE IF NOT EXISTS payments;

USE payments;

-- ------------------------------------------------------------
-- PAYMENTS
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS payments (
    id            CHAR(36)     NOT NULL,
    order_id      CHAR(36)     NOT NULL,
    amount        INT          NOT NULL,
    payment_type  VARCHAR(20)  NOT NULL,
    status        VARCHAR(20)  NOT NULL,
    reason        VARCHAR(100) NULL,
    created_at    DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),

    PRIMARY KEY (id),

    CONSTRAINT uq_payments_order_id UNIQUE (order_id),

    CONSTRAINT chk_payments_status       CHECK (status IN ('AUTHORIZED', 'DENIED')),
    CONSTRAINT chk_payments_payment_type CHECK (payment_type IN ('PIX', 'CREDIT_CARD', 'BOLETO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- OUTBOX EVENTS (Transactional Outbox Pattern)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS outbox_events (
    id              CHAR(36)     NOT NULL,
    aggregate_type  VARCHAR(50)  NOT NULL,
    aggregate_id    CHAR(36)     NOT NULL,
    event_type      VARCHAR(100) NOT NULL,
    payload         JSON         NOT NULL,
    trace_parent    VARCHAR(255) NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    retries_count   INT          NOT NULL DEFAULT 0,
    max_retries     INT          NOT NULL DEFAULT 5,
    created_at      DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    sent_at         DATETIME(3)  NULL,
    locked_at       DATETIME(3)  NULL,

    PRIMARY KEY (id),

    CONSTRAINT chk_outbox_status CHECK (status IN ('PENDING', 'PROCESSING', 'SENT', 'FAILED', 'DEAD_LETTER'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_outbox_relay ON outbox_events (status, created_at);
CREATE INDEX idx_outbox_aggregate ON outbox_events (aggregate_type, aggregate_id);
