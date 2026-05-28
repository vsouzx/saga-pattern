package br.com.souza.inventory_service.application.domain.model

import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StockTest {

    @Test
    fun `should create stock with all fields`() {
        val now = LocalDateTime.of(2026, 5, 23, 10, 0)
        val stock = Stock(
            id = "abc-123",
            productId = 42,
            quantityAvailable = 100,
            updatedAt = now
        )

        assertEquals("abc-123", stock.id)
        assertEquals(42, stock.productId)
        assertEquals(100, stock.quantityAvailable)
        assertEquals(now, stock.updatedAt)
    }

    @Test
    fun `should create stock with null id by default`() {
        val stock = Stock(
            productId = 42,
            quantityAvailable = 100,
            updatedAt = LocalDateTime.now()
        )

        assertNull(stock.id)
    }

    @Test
    fun `should support copy with modified quantity`() {
        val now = LocalDateTime.of(2026, 5, 23, 10, 0)
        val stock = Stock(id = "abc-123", productId = 42, quantityAvailable = 100, updatedAt = now)
        val updated = stock.copy(quantityAvailable = 80)

        assertEquals(80, updated.quantityAvailable)
        assertEquals(stock.id, updated.id)
        assertEquals(stock.productId, updated.productId)
    }
}
