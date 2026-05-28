package domain

import "time"

type PaymentType string
type PaymentStatus string

const (
	PaymentTypePix        PaymentType = "PIX"
	PaymentTypeCreditCard PaymentType = "CREDIT_CARD"
	PaymentTypeBoleto     PaymentType = "BOLETO"

	PaymentStatusAuthorized PaymentStatus = "AUTHORIZED"
	PaymentStatusDenied     PaymentStatus = "DENIED"
)

type Payment struct {
	ID          string
	OrderID     string
	Amount      int
	PaymentType PaymentType
	Status      PaymentStatus
	Reason      string
	CreatedAt   time.Time
}

// InventoryReservedEvent is the event consumed from the inventory.reserved topic.
type InventoryReservedEvent struct {
	OrderID     string `json:"orderId"`
	Amount      int    `json:"amount"`
	PaymentType string `json:"paymentType"`
	CreatedAt   string `json:"createdAt"`
}

// PaymentAuthorizedEvent is published to payments.authorized.
type PaymentAuthorizedEvent struct {
	PaymentID string `json:"paymentId"`
	OrderID   string `json:"orderId"`
	Status    string `json:"status"`
	CreatedAt string `json:"createdAt"`
}

// PaymentDeniedEvent is published to payments.denied.
type PaymentDeniedEvent struct {
	PaymentID string `json:"paymentId"`
	OrderID   string `json:"orderId"`
	Status    string `json:"status"`
	Reason    string `json:"reason"`
	CreatedAt string `json:"createdAt"`
}
