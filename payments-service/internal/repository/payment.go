package repository

import (
	"context"
	"database/sql"
	"errors"
	"fmt"
	"payments-service/internal/domain"

	"github.com/go-sql-driver/mysql"
	"github.com/google/uuid"
)

type PaymentRepositoryInterface interface {
	Save(ctx context.Context, tx DBTX, payment domain.Payment) error
	ExistsByOrderID(ctx context.Context, orderID string) (bool, error)
}

type PaymentRepository struct {
	db *sql.DB
}

func NewPaymentRepository(db *sql.DB) *PaymentRepository {
	return &PaymentRepository{db: db}
}

func (r *PaymentRepository) Save(ctx context.Context, tx DBTX, payment domain.Payment) error {
	query := `INSERT INTO payments (id, order_id, amount, payment_type, status, reason, created_at)
	          VALUES (?, ?, ?, ?, ?, ?, NOW())`

	if payment.ID == "" {
		payment.ID = uuid.New().String()
	}

	_, err := tx.ExecContext(ctx, query,
		payment.ID, payment.OrderID, payment.Amount,
		payment.PaymentType, payment.Status, nullableString(payment.Reason),
	)
	if err != nil {
		var mysqlErr *mysql.MySQLError
		if errors.As(err, &mysqlErr) && mysqlErr.Number == 1062 {
			return fmt.Errorf("duplicate payment for order %s", payment.OrderID)
		}
		return fmt.Errorf("saving payment: %w", err)
	}
	return nil
}

func (r *PaymentRepository) ExistsByOrderID(ctx context.Context, orderID string) (bool, error) {
	var exists bool
	query := `SELECT EXISTS(SELECT 1 FROM payments WHERE order_id = ?)`

	err := r.db.QueryRowContext(ctx, query, orderID).Scan(&exists)
	if err != nil {
		return false, fmt.Errorf("checking payment existence: %w", err)
	}
	return exists, nil
}

func nullableString(s string) *string {
	if s == "" {
		return nil
	}
	return &s
}
