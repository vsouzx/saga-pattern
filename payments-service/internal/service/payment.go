package service

import (
	"context"
	"database/sql"
	"encoding/json"
	"fmt"
	"payments-service/internal/domain"
	"payments-service/internal/logger"
	"payments-service/internal/repository"
	"time"

	"github.com/google/uuid"
	"go.uber.org/zap"
)

type PaymentService struct {
	logger      *zap.Logger
	db          *sql.DB
	paymentRepo repository.PaymentRepositoryInterface
	outboxRepo  repository.OutboxRepositoryInterface
}

func NewPaymentService(
	logger *zap.Logger,
	db *sql.DB,
	paymentRepo repository.PaymentRepositoryInterface,
	outboxRepo repository.OutboxRepositoryInterface,
) *PaymentService {
	return &PaymentService{
		logger:      logger,
		db:          db,
		paymentRepo: paymentRepo,
		outboxRepo:  outboxRepo,
	}
}

func (s *PaymentService) ProcessPayment(ctx context.Context, event domain.InventoryReservedEvent, traceParent string) error {
	log := logger.FromContext(ctx, s.logger)

	// idempotency check
	exists, err := s.paymentRepo.ExistsByOrderID(ctx, event.OrderID)
	if err != nil {
		return fmt.Errorf("checking idempotency: %w", err)
	}
	if exists {
		log.Info("payment already processed, skipping", zap.String("order_id", event.OrderID))
		return nil
	}

	// evaluate business rules
	status, reason := s.evaluate(event)

	paymentID := uuid.New().String()
	now := time.Now()

	payment := domain.Payment{
		ID:          paymentID,
		OrderID:     event.OrderID,
		Amount:      event.Amount,
		PaymentType: domain.PaymentType(event.PaymentType),
		Status:      status,
		Reason:      reason,
		CreatedAt:   now,
	}

	// build outbox event payload
	var eventType string
	var payload []byte

	if status == domain.PaymentStatusAuthorized {
		eventType = "payments.authorized"
		payload, err = json.Marshal(domain.PaymentAuthorizedEvent{
			PaymentID: paymentID,
			OrderID:   event.OrderID,
			Status:    string(status),
			CreatedAt: now.Format(time.RFC3339),
		})
	} else {
		eventType = "payments.denied"
		payload, err = json.Marshal(domain.PaymentDeniedEvent{
			PaymentID: paymentID,
			OrderID:   event.OrderID,
			Status:    string(status),
			Reason:    reason,
			CreatedAt: now.Format(time.RFC3339),
		})
	}
	if err != nil {
		return fmt.Errorf("marshalling event payload: %w", err)
	}

	outboxEvent := domain.OutboxEvent{
		AggregateType: "PAYMENT",
		AggregateID:   paymentID,
		EventType:     eventType,
		Payload:       payload,
		TraceParent:   traceParent,
		Status:        "PENDING",
		RetriesCount:  0,
		MaxRetries:    5,
	}

	// begin transaction
	tx, err := s.db.BeginTx(ctx, nil)
	if err != nil {
		return fmt.Errorf("beginning transaction: %w", err)
	}
	defer tx.Rollback()

	if err := s.paymentRepo.Save(ctx, tx, payment); err != nil {
		return fmt.Errorf("saving payment: %w", err)
	}

	if err := s.outboxRepo.Save(ctx, tx, outboxEvent); err != nil {
		return fmt.Errorf("saving outbox event: %w", err)
	}

	if err := tx.Commit(); err != nil {
		return fmt.Errorf("committing transaction: %w", err)
	}

	log.Info("payment processed",
		zap.String("payment_id", paymentID),
		zap.String("order_id", event.OrderID),
		zap.String("status", string(status)),
	)

	return nil
}

func (s *PaymentService) evaluate(event domain.InventoryReservedEvent) (domain.PaymentStatus, string) {
	if event.PaymentType == string(domain.PaymentTypeBoleto) {
		return domain.PaymentStatusDenied, "BOLETO_NOT_ACCEPTED"
	}

	if event.PaymentType == string(domain.PaymentTypeCreditCard) && event.Amount > 10000 {
		return domain.PaymentStatusDenied, "AMOUNT_EXCEEDED"
	}

	return domain.PaymentStatusAuthorized, ""
}
