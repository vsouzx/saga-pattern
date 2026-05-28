package br.com.souza.inventory_service.adapter.out.mappers

import br.com.souza.inventory_service.adapter.out.reservation.models.StockReservationJpaEntity
import br.com.souza.inventory_service.adapter.out.reservation.mappers.toDomain
import br.com.souza.inventory_service.adapter.out.reservation.mappers.toJpaEntity
import br.com.souza.inventory_service.application.domain.model.ReservationStatus
import br.com.souza.inventory_service.application.domain.model.StockReservation
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class StockReservationMapperTest {

    @Test
    fun `should map StockReservation to JpaEntity`() {
        val now = LocalDateTime.now()
        val domain = StockReservation(
            id = "res-1",
            orderId = "order-1",
            productId = 1,
            quantity = 3,
            status = ReservationStatus.RESERVED,
            createdAt = now
        )

        val entity = domain.toJpaEntity()

        assertEquals("res-1", entity.id)
        assertEquals("order-1", entity.orderId)
        assertEquals(1, entity.productId)
        assertEquals(3, entity.quantity)
        assertEquals("RESERVED", entity.status)
        assertEquals(now, entity.createdAt)
    }

    @Test
    fun `should map JpaEntity to StockReservation`() {
        val now = LocalDateTime.now()
        val entity = StockReservationJpaEntity(
            id = "res-1",
            orderId = "order-1",
            productId = 1,
            quantity = 3,
            status = "RESERVED",
            createdAt = now
        )

        val domain = entity.toDomain()

        assertEquals("res-1", domain.id)
        assertEquals("order-1", domain.orderId)
        assertEquals(1, domain.productId)
        assertEquals(3, domain.quantity)
        assertEquals(ReservationStatus.RESERVED, domain.status)
        assertEquals(now, domain.createdAt)
    }
}
