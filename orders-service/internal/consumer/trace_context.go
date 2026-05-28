package consumer

import (
	"context"

	"github.com/segmentio/kafka-go"
	"go.opentelemetry.io/otel"
	"go.opentelemetry.io/otel/propagation"
)

func extractTraceContext(ctx context.Context, headers []kafka.Header) context.Context {
	carrier := make(propagation.MapCarrier)
	for _, h := range headers {
		carrier.Set(h.Key, string(h.Value))
	}

	propagator := otel.GetTextMapPropagator()
	return propagator.Extract(ctx, carrier)
}
