package br.com.souza.inventory_service.adapter.out.mappers

import br.com.souza.inventory_service.adapter.out.relay.mappers.toDomain
import br.com.souza.inventory_service.adapter.out.relay.mappers.toJpaEntity
import br.com.souza.inventory_service.adapter.out.relay.models.OutboxEventJpaEntity
import br.com.souza.inventory_service.application.domain.model.OutboxEvent
import br.com.souza.inventory_service.application.domain.model.OutboxEventStatus
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class OutboxEventMapperTest {

    @Test
    fun `should map OutboxEvent to JpaEntity`() {
        val now = LocalDateTime.now()
        val domain = OutboxEvent(
            id = "abc-123",
            aggregateId = "order:order-1",
            aggregateType = "ORDER",
            eventType = "INVENTORY_RESERVED",
            topic = "inventory.reserved",
            payload = """{"orderId":"order-1"}""",
            traceParent = "00-trace-span-01",
            status = OutboxEventStatus.PENDING,
            retriesCount = 0,
            maxRetries = 5,
            createdAt = now,
            sentAt = null,
            lockedAt = null
        )

        val entity = domain.toJpaEntity()

        assertEquals("abc-123", entity.id)
        assertEquals("order:order-1", entity.aggregateId)
        assertEquals("ORDER", entity.aggregateType)
        assertEquals("INVENTORY_RESERVED", entity.eventType)
        assertEquals("inventory.reserved", entity.topic)
        assertEquals("""{"orderId":"order-1"}""", entity.payload)
        assertEquals("00-trace-span-01", entity.traceParent)
        assertEquals("PENDING", entity.status)
        assertEquals(0, entity.retriesCount)
        assertEquals(5, entity.maxRetries)
        assertEquals(now, entity.createdAt)
        assertNull(entity.sentAt)
        assertNull(entity.lockedAt)
    }

    @Test
    fun `should map JpaEntity to OutboxEvent`() {
        val now = LocalDateTime.now()
        val entity = OutboxEventJpaEntity(
            id = "abc-123",
            aggregateId = "order:order-1",
            aggregateType = "ORDER",
            eventType = "INVENTORY_RESERVED",
            topic = "inventory.reserved",
            payload = """{"orderId":"order-1"}""",
            traceParent = "00-trace-span-01",
            status = "PENDING",
            retriesCount = 0,
            maxRetries = 5,
            createdAt = now,
            sentAt = null,
            lockedAt = null
        )

        val domain = entity.toDomain()

        assertEquals("abc-123", domain.id)
        assertEquals("order:order-1", domain.aggregateId)
        assertEquals("ORDER", domain.aggregateType)
        assertEquals("INVENTORY_RESERVED", domain.eventType)
        assertEquals("inventory.reserved", domain.topic)
        assertEquals("""{"orderId":"order-1"}""", domain.payload)
        assertEquals("00-trace-span-01", domain.traceParent)
        assertEquals(OutboxEventStatus.PENDING, domain.status)
        assertEquals(0, domain.retriesCount)
        assertEquals(5, domain.maxRetries)
        assertEquals(now, domain.createdAt)
        assertNull(domain.sentAt)
        assertNull(domain.lockedAt)
    }
}
