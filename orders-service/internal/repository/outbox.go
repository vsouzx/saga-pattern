package repository

import (
	"context"
	"database/sql"
	"fmt"
	"orders-service/internal/domain"

	"github.com/google/uuid"
)

type OutboxRepositoryInterface interface {
	Save(ctx context.Context, tx DBTX, model domain.OutboxModel) error
	FindAndLockPendingEvents(ctx context.Context, limit int) ([]domain.OutboxModel, error)
	MarkEventAsSent(ctx context.Context, id string) error
	MarkEventAsFailed(ctx context.Context, id string) error
}

type OutboxRepository struct {
	db *sql.DB
}

func NewOutboxRepository(db *sql.DB) *OutboxRepository {
	return &OutboxRepository{
		db: db,
	}
}

func (or *OutboxRepository) Save(ctx context.Context, tx DBTX, model domain.OutboxModel) error {
	query := `INSERT INTO outbox (id, aggregate_type, aggregate_id, event_type, payload, trace_parent, status, retries_count, max_retries, created_at, sent_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), null)`

	id := uuid.New().String()
	_, err := tx.ExecContext(ctx, query, id, model.AggregateType, model.AggregateId, model.EventType, model.Payload, model.TraceParent, model.Status, model.RetriesCount, model.MaxRetries)
	if err != nil {
		return fmt.Errorf("saving outbox event: %w", err)
	}
	return nil
}

func (or *OutboxRepository) FindAndLockPendingEvents(ctx context.Context, limit int) ([]domain.OutboxModel, error) {
	tx, err := or.db.BeginTx(ctx, nil)
	if err != nil {
		return nil, fmt.Errorf("beginning outbox transaction: %w", err)
	}
	defer tx.Rollback()

	rows, err := tx.QueryContext(ctx, `SELECT id, aggregate_type, aggregate_id, event_type, payload, trace_parent, status, retries_count, max_retries, created_at, sent_at
			  FROM outbox
			  WHERE status IN ('PENDING', 'FAILED') AND retries_count < max_retries
			  	OR (status = 'PROCESSING' AND locked_at < NOW() - INTERVAL 5 MINUTE) 
			  ORDER BY created_at ASC
			  LIMIT ?
			  FOR UPDATE SKIP LOCKED
			  `, limit)
	if err != nil {
		return nil, fmt.Errorf("querying pending outbox events: %w", err)
	}
	defer rows.Close()

	var events []domain.OutboxModel
	var ids []string

	for rows.Next() {
		var event domain.OutboxModel
		err := rows.Scan(
			&event.ID,
			&event.AggregateType,
			&event.AggregateId,
			&event.EventType,
			&event.Payload,
			&event.TraceParent,
			&event.Status,
			&event.RetriesCount,
			&event.MaxRetries,
			&event.CreatedAt,
			&event.SentAt,
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
			UPDATE outbox
			SET status = 'PROCESSING',
			    locked_at = NOW()
			WHERE id = ?
			`, id)
		if err != nil {
			return nil, err
		}
	}

	if err := tx.Commit(); err != nil {
		return nil, fmt.Errorf("committing outbox transaction: %w", err)
	}

	return events, nil
}

func (or *OutboxRepository) MarkEventAsSent(ctx context.Context, id string) error {
	query := `UPDATE outbox SET status = 'SENT', sent_at = NOW() WHERE id = ?`

	_, err := or.db.ExecContext(ctx, query, id)
	if err != nil {
		return fmt.Errorf("marking event as sent: %w", err)
	}
	return nil
}

func (or *OutboxRepository) MarkEventAsFailed(ctx context.Context, id string) error {
	query := `UPDATE outbox SET status = CASE WHEN retries_count + 1 >= max_retries THEN 'DEAD_LETTER' ELSE 'FAILED' END, retries_count = retries_count + 1 WHERE id = ?`

	_, err := or.db.ExecContext(ctx, query, id)
	if err != nil {
		return fmt.Errorf("marking event as failed: %w", err)
	}
	return nil
}
