package br.com.souza.inventory_service.infrastructure.observability

import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.context.Context
import io.opentelemetry.context.propagation.TextMapGetter
import org.springframework.stereotype.Component

@Component
class TraceContextExtractor {

    fun extractContext(traceParent: String?): Context {
        if (traceParent == null) return Context.current()

        val carrier = mapOf("traceparent" to traceParent)
        val getter = object : TextMapGetter<Map<String, String>> {
            override fun keys(carrier: Map<String, String>): Iterable<String> = carrier.keys
            override fun get(carrier: Map<String, String>?, key: String): String? = carrier?.get(key)
        }

        return GlobalOpenTelemetry.getPropagators()
            .textMapPropagator
            .extract(Context.current(), carrier, getter)
    }
}
