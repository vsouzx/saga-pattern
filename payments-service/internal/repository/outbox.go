package repository

import (
	"context"
	"database/sql"
	"fmt"
	"payments-service/internal/domain"

	"github.com/google/uuid"
)

type OutboxRepositoryInterface interface {
	Save(ctx context.Context, tx DBTX, event domain.OutboxEvent) error
	FindAndLockPendingEvents(ctx context.Context, limit int) ([]domain.OutboxEvent, error)
	MarkEventAsSent(ctx context.Context, id string) error
	MarkEventAsFailed(ctx context.Context, id string) error
}

type OutboxRepository struct {
	db *sql.DB
}

func NewOutboxRepository(db *sql.DB) *OutboxRepository {
	return &OutboxRepository{db: db}
}

func (r *OutboxRepository) Save(ctx context.Context, tx DBTX, event domain.OutboxEvent) error {
	query := `INSERT INTO outbox_events (id, aggregate_type, aggregate_id, event_type, payload, trace_parent, status, retries_count, max_retries, created_at, sent_at)
	          VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NULL)`

	id := uuid.New().String()
	_, err := tx.ExecContext(ctx, query,
		id, event.AggregateType, event.AggregateID,
		event.EventType, event.Payload, event.TraceParent,
		event.Status, event.RetriesCount, event.MaxRetries,
	)
	if err != nil {
		return fmt.Errorf("saving outbox event: %w", err)
	}
	return nil
}

func (r *OutboxRepository) FindAndLockPendingEvents(ctx context.Context, limit int) ([]domain.OutboxEvent, error) {
	tx, err := r.db.BeginTx(ctx, nil)
	if err != nil {
		return nil, fmt.Errorf("beginning outbox transaction: %w", err)
	}
	defer tx.Rollback()

	rows, err := tx.QueryContext(ctx, `
		SELECT id, aggregate_type, aggregate_id, event_type, payload, trace_parent,
		       status, retries_count, max_retries, created_at, sent_at
		FROM outbox_events
		WHERE (status IN ('PENDING', 'FAILED') AND retries_count < max_retries)
		   OR (status = 'PROCESSING' AND locked_at < NOW() - INTERVAL 5 MINUTE)
		ORDER BY created_at ASC
		LIMIT ?
		FOR UPDATE SKIP LOCKED`, limit)
	if err != nil {
		return nil, fmt.Errorf("querying pending outbox events: %w", err)
	}
	defer rows.Close()

	var events []domain.OutboxEvent
	var ids []string

	for rows.Next() {
		var event domain.OutboxEvent
		err := rows.Scan(
			&event.ID, &event.AggregateType, &event.AggregateID,
			&event.EventType, &event.Payload, &event.TraceParent,
			&event.Status, &event.RetriesCount, &event.MaxRetries,
			&event.CreatedAt, &event.SentAt,
		)
		if err != nil {
			return nil, fmt.Errorf("scanning outbox row: %w", err)
		}
		events = append(events, event)
		ids = append(ids, event.ID)
	}

	if err := rows.Err(); err != nil {
		return nil, fmt.Errorf("iterating outbox rows: %w", err)
	}

	for _, id := range ids {
		_, err = tx.ExecContext(ctx, `
			UPDATE outbox_events
			SET status = 'PROCESSING', locked_at = NOW()
			WHERE id = ?`, id)
		if err != nil {
			return nil, fmt.Errorf("locking outbox event: %w", err)
		}
	}

	if err := tx.Commit(); err != nil {
		return nil, fmt.Errorf("committing outbox transaction: %w", err)
	}

	return events, nil
}

func (r *OutboxRepository) MarkEventAsSent(ctx context.Context, id string) error {
	query := `UPDATE outbox_events SET status = 'SENT', sent_at = NOW() WHERE id = ?`
	_, err := r.db.ExecContext(ctx, query, id)
	if err != nil {
		return fmt.Errorf("marking event as sent: %w", err)
	}
	return nil
}

func (r *OutboxRepository) MarkEventAsFailed(ctx context.Context, id string) error {
	query := `UPDATE outbox_events
	          SET status = CASE WHEN retries_count + 1 >= max_retries THEN 'DEAD_LETTER' ELSE 'FAILED' END,
	              retries_count = retries_count + 1
	          WHERE id = ?`
	_, err := r.db.ExecContext(ctx, query, id)
	if err != nil {
		return fmt.Errorf("marking event as failed: %w", err)
	}
	return nil
}
