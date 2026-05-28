package domain

import (
	"encoding/json"
	"time"
)

type OutboxEvent struct {
	ID            string
	AggregateType string
	AggregateID   string
	EventType     string
	Payload       json.RawMessage
	TraceParent   string
	Status        string
	RetriesCount  int
	MaxRetries    int
	CreatedAt     time.Time
	SentAt        *time.Time
	LockedAt      *time.Time
}
