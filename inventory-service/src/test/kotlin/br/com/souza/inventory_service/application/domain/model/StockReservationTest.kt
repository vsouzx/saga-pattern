package br.com.souza.inventory_service.application.domain.model

import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StockReservationTest {

    @Test
    fun `should create reservation with all fields`() {
        val now = LocalDateTime.of(2026, 5, 23, 10, 0, 0)
        val reservation = StockReservation(
            id = "550e8400-e29b-41d4-a716-446655440000",
            orderId = "order-123",
            productId = 1,
            quantity = 5,
            status = ReservationStatus.RESERVED,
            createdAt = now
        )

        assertEquals("550e8400-e29b-41d4-a716-446655440000", reservation.id)
        assertEquals("order-123", reservation.orderId)
        assertEquals(1, reservation.productId)
        assertEquals(5, reservation.quantity)
        assertEquals(ReservationStatus.RESERVED, reservation.status)
        assertEquals(now, reservation.createdAt)
    }

    @Test
    fun `should create reservation with null id by default`() {
        val reservation = StockReservation(
            orderId = "order-456",
            productId = 2,
            quantity = 3,
            status = ReservationStatus.RESERVED,
            createdAt = LocalDateTime.now()
        )

        assertNull(reservation.id)
    }

    @Test
    fun `should support copy with modified status`() {
        val reservation = StockReservation(
            id = "abc-123",
            orderId = "order-789",
            productId = 1,
            quantity = 2,
            status = ReservationStatus.RESERVED,
            createdAt = LocalDateTime.of(2026, 5, 23, 10, 0, 0)
        )

        val confirmed = reservation.copy(status = ReservationStatus.CONFIRMED)
        assertEquals(ReservationStatus.CONFIRMED, confirmed.status)
        assertEquals(reservation.id, confirmed.id)
        assertEquals(reservation.orderId, confirmed.orderId)

        val released = reservation.copy(status = ReservationStatus.RELEASED)
        assertEquals(ReservationStatus.RELEASED, released.status)
    }
}
