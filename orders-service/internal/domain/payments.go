package domain

type PaymentStatus string

const (
	PaymentStatusAuthorized PaymentStatus = "AUTHORIZED"
	PaymentStatusDenied     PaymentStatus = "DENIED"
)

// PaymentsAuthorizedEvent is consumed from payments.authorized topic
type PaymentsAuthorizedEvent struct {
	Status    string `json:"status"`
	OrderID   string `json:"orderId"`
	PaymentID string `json:"paymentId"`
	CreatedAt string `json:"createdAt"`
}

// PaymentsDeniedEvent is consumed from payments.denied topic
type PaymentsDeniedEvent struct {
	Status    string `json:"status"`
	OrderID   string `json:"orderId"`
	PaymentID string `json:"paymentId"`
	Reason    string `json:"reason"`
	CreatedAt string `json:"createdAt"`
}
