package relay

import (
	"context"
	"fmt"
	"payments-service/internal/logger"
	"payments-service/internal/repository"
	"time"

	"github.com/segmentio/kafka-go"
	"go.uber.org/zap"
)

type OutboxRelay struct {
	logger     *zap.Logger
	outboxRepo repository.OutboxRepositoryInterface
	writers    map[string]*kafka.Writer
	batchSize  int
	interval   time.Duration
}

func NewOutboxRelay(
	logger *zap.Logger,
	outboxRepo repository.OutboxRepositoryInterface,
	writers map[string]*kafka.Writer,
	batchSize int,
) *OutboxRelay {
	return &OutboxRelay{
		logger:     logger,
		outboxRepo: outboxRepo,
		writers:    writers,
		batchSize:  batchSize,
		interval:   5 * time.Second,
	}
}

func (r *OutboxRelay) Start(ctx context.Context) {
	r.logger.Info("outbox relay started")
	ticker := time.NewTicker(r.interval)
	defer ticker.Stop()

	for {
		select {
		case <-ctx.Done():
			r.logger.Info("outbox relay stopped")
			return
		case <-ticker.C:
			r.poll(ctx)
		}
	}
}

func (r *OutboxRelay) poll(ctx context.Context) {
	log := logger.FromContext(ctx, r.logger)

	events, err := r.outboxRepo.FindAndLockPendingEvents(ctx, r.batchSize)
	if err != nil {
		log.Error("error finding pending events", zap.Error(err))
		return
	}

	for _, event := range events {
		log.Info("sending event",
			zap.String("outbox_event_id", event.ID),
			zap.String("event_type", event.EventType),
			zap.Int("retries_count", event.RetriesCount),
		)

		writer, ok := r.writers[event.EventType]
		if !ok {
			log.Error("no kafka writer for event type", zap.String("event_type", event.EventType))
			if err := r.outboxRepo.MarkEventAsFailed(ctx, event.ID); err != nil {
				log.Error("error marking event as failed", zap.Error(err))
			}
			continue
		}

		message := kafka.Message{
			Key:   []byte(fmt.Sprintf("%s-%s", event.AggregateType, event.AggregateID)),
			Value: event.Payload,
			Headers: []kafka.Header{
				{Key: "traceparent", Value: []byte(event.TraceParent)},
			},
		}

		if err := writer.WriteMessages(ctx, message); err != nil {
			log.Error("error writing outbox event",
				zap.String("outbox_event_id", event.ID),
				zap.Error(err),
			)
			if err := r.outboxRepo.MarkEventAsFailed(ctx, event.ID); err != nil {
				log.Error("error marking event as failed", zap.Error(err))
			}
			continue
		}

		if err := r.outboxRepo.MarkEventAsSent(ctx, event.ID); err != nil {
			log.Error("error marking event as sent", zap.Error(err))
		} else {
			log.Info("event sent", zap.String("outbox_event_id", event.ID))
		}
	}
}
