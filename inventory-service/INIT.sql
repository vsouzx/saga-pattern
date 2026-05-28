-- =============================================
-- Inventory Service - Database Init Script
-- =============================================

CREATE TABLE IF NOT EXISTS products (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    price       INTEGER NOT NULL CHECK (price >= 0)
);

CREATE TABLE IF NOT EXISTS stocks (
    id                  VARCHAR(36) PRIMARY KEY,
    product_id          INTEGER NOT NULL UNIQUE REFERENCES products(id),
    quantity_available   INTEGER NOT NULL CHECK (quantity_available >= 0),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS stock_reservations (
    id          VARCHAR(36) PRIMARY KEY,
    order_id    VARCHAR(36) NOT NULL,
    product_id  INTEGER NOT NULL REFERENCES products(id),
    quantity    INTEGER NOT NULL CHECK (quantity > 0),
    status      VARCHAR(50) NOT NULL DEFAULT 'RESERVED',
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_reservation_status CHECK (status IN ('RESERVED', 'CONFIRMED', 'RELEASED'))
);

CREATE INDEX idx_stock_reservations_order_id ON stock_reservations(order_id);
CREATE INDEX idx_stock_reservations_product_id ON stock_reservations(product_id);
CREATE INDEX idx_stock_reservations_status ON stock_reservations(status);

CREATE TABLE IF NOT EXISTS outbox_events (
    id              VARCHAR(36) PRIMARY KEY,
    aggregate_id    VARCHAR(36) NOT NULL,
    aggregate_type  VARCHAR(100) NOT NULL,
    event_type      VARCHAR(100) NOT NULL,
    topic           VARCHAR(255) NOT NULL,
    payload         TEXT NOT NULL,
    trace_parent    VARCHAR(255),
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    retries_count   INTEGER NOT NULL DEFAULT 0,
    max_retries     INTEGER NOT NULL DEFAULT 5,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    sent_at         TIMESTAMP,
    locked_at       TIMESTAMP,

    CONSTRAINT chk_outbox_status CHECK (status IN ('PENDING', 'PROCESSING', 'SENT', 'FAILED', 'DEAD_LETTER')),
    CONSTRAINT chk_retries CHECK (retries_count >= 0 AND retries_count <= max_retries)
);

-- existsByAggregateId() - idempotency check no ReserveStockService
CREATE INDEX idx_outbox_events_aggregate_id ON outbox_events(aggregate_id);

-- findPendingEvents() - busca eventos pendentes para relay ao Kafka
CREATE INDEX idx_outbox_events_pending ON outbox_events(status, locked_at, created_at)
    WHERE status IN ('PENDING', 'FAILED');
