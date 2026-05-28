package domain

import (
	"encoding/json"
	"time"
)

type OutboxModel struct {
	ID            string
	AggregateType string
	AggregateId   string
	EventType     string
	Payload       json.RawMessage
	TraceParent   string
	Status        string
	RetriesCount  int
	MaxRetries    int
	CreatedAt     time.Time
	SentAt        *time.Time
}
