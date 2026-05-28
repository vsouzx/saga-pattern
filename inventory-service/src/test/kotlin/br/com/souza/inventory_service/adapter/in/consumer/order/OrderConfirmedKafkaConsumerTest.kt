package br.com.souza.inventory_service.adapter.`in`.consumer.order

import br.com.souza.inventory_service.adapter.`in`.consumer.order.dto.OrderConfirmedEvent
import br.com.souza.inventory_service.application.domain.model.ConfirmReservationCommand
import br.com.souza.inventory_service.application.ports.`in`.ConfirmReservationUseCase
import br.com.souza.inventory_service.infrastructure.observability.TraceContextExtractor
import io.opentelemetry.context.Context
import org.mockito.kotlin.*
import kotlin.test.Test

class OrderConfirmedKafkaConsumerTest {

    private val confirmReservation = mock<ConfirmReservationUseCase>()
    private val traceContextExtractor = mock<TraceContextExtractor>()

    private val consumer = OrderConfirmedKafkaConsumer(confirmReservation, traceContextExtractor)

    @Test
    fun `should delegate to ConfirmReservationUseCase with correct command`() {
        val event = OrderConfirmedEvent(
            status = "CONFIRMED",
            orderId = "order-1",
            timestamp = "2026-05-27T01:02:27.101165-03:00"
        )
        val traceParent = "00-trace-span-01"

        whenever(traceContextExtractor.extractContext(traceParent)).thenReturn(Context.root())

        consumer.consume(event, traceParent)

        verify(confirmReservation).execute(argThat {
            orderId == "order-1" && this.traceParent == "00-trace-span-01"
        })
    }

    @Test
    fun `should handle null traceparent`() {
        val event = OrderConfirmedEvent(
            status = "CONFIRMED",
            orderId = "order-2",
            timestamp = "2026-05-27T01:02:27.101165-03:00"
        )

        whenever(traceContextExtractor.extractContext(null)).thenReturn(Context.root())

        consumer.consume(event, null)

        verify(confirmReservation).execute(argThat {
            orderId == "order-2" && this.traceParent == null
        })
    }
}
