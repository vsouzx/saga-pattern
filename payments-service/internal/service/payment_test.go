package service

import (
	"testing"

	"payments-service/internal/domain"
)

func TestEvaluate_BoletoAlwaysDenied(t *testing.T) {
	s := &PaymentService{}
	status, reason := s.evaluate(domain.InventoryReservedEvent{
		PaymentType: "BOLETO",
		Amount:      100,
	})
	if status != domain.PaymentStatusDenied {
		t.Errorf("expected DENIED, got %s", status)
	}
	if reason != "BOLETO_NOT_ACCEPTED" {
		t.Errorf("expected BOLETO_NOT_ACCEPTED, got %s", reason)
	}
}

func TestEvaluate_CreditCardAbove10000Denied(t *testing.T) {
	s := &PaymentService{}
	status, reason := s.evaluate(domain.InventoryReservedEvent{
		PaymentType: "CREDIT_CARD",
		Amount:      10001,
	})
	if status != domain.PaymentStatusDenied {
		t.Errorf("expected DENIED, got %s", status)
	}
	if reason != "AMOUNT_EXCEEDED" {
		t.Errorf("expected AMOUNT_EXCEEDED, got %s", reason)
	}
}

func TestEvaluate_CreditCardAtOrBelow10000Authorized(t *testing.T) {
	s := &PaymentService{}
	status, reason := s.evaluate(domain.InventoryReservedEvent{
		PaymentType: "CREDIT_CARD",
		Amount:      10000,
	})
	if status != domain.PaymentStatusAuthorized {
		t.Errorf("expected AUTHORIZED, got %s", status)
	}
	if reason != "" {
		t.Errorf("expected empty reason, got %s", reason)
	}
}

func TestEvaluate_PixAlwaysAuthorized(t *testing.T) {
	s := &PaymentService{}
	status, reason := s.evaluate(domain.InventoryReservedEvent{
		PaymentType: "PIX",
		Amount:      999999,
	})
	if status != domain.PaymentStatusAuthorized {
		t.Errorf("expected AUTHORIZED, got %s", status)
	}
	if reason != "" {
		t.Errorf("expected empty reason, got %s", reason)
	}
}
