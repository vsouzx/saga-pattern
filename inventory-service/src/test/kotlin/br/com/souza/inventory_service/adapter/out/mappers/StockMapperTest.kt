package br.com.souza.inventory_service.adapter.out.mappers

import br.com.souza.inventory_service.adapter.out.stock.models.StockJpaEntity
import br.com.souza.inventory_service.adapter.out.stock.mappers.toDomain
import br.com.souza.inventory_service.adapter.out.stock.mappers.toJpaEntity
import br.com.souza.inventory_service.application.domain.model.Stock
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

class StockMapperTest {

    private val fixedTime = LocalDateTime.of(2026, 5, 23, 10, 0)

    @Test
    fun `should convert JPA entity to domain model`() {
        val jpaEntity = StockJpaEntity(
            id = "stock-1",
            productId = 42,
            quantityAvailable = 100,
            updatedAt = fixedTime
        )

        val domain = jpaEntity.toDomain()

        assertEquals("stock-1", domain.id)
        assertEquals(42, domain.productId)
        assertEquals(100, domain.quantityAvailable)
        assertEquals(fixedTime, domain.updatedAt)
    }

    @Test
    fun `should convert domain model to JPA entity`() {
        val stock = Stock(
            id = "stock-1",
            productId = 42,
            quantityAvailable = 100,
            updatedAt = fixedTime
        )

        val jpaEntity = stock.toJpaEntity()

        assertEquals("stock-1", jpaEntity.id)
        assertEquals(42, jpaEntity.productId)
        assertEquals(100, jpaEntity.quantityAvailable)
        assertEquals(fixedTime, jpaEntity.updatedAt)
    }

    @Test
    fun `should convert domain model with null id to JPA entity with empty id`() {
        val stock = Stock(
            productId = 42,
            quantityAvailable = 100,
            updatedAt = fixedTime
        )

        val jpaEntity = stock.toJpaEntity()

        assertEquals("", jpaEntity.id)
    }
}
