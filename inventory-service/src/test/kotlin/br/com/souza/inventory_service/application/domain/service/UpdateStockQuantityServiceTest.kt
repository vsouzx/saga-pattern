package br.com.souza.inventory_service.application.domain.service

import br.com.souza.inventory_service.application.domain.model.Stock
import br.com.souza.inventory_service.application.ports.out.StockRepositoryPort
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import kotlin.test.assertEquals

class UpdateStockQuantityServiceTest {

    private val existingStock = Stock(
        id = "stock-1",
        productId = 42,
        quantityAvailable = 100,
        updatedAt = LocalDateTime.of(2026, 5, 23, 10, 0)
    )

    private val fakeRepository = object : StockRepositoryPort {
        override fun save(stock: Stock): Stock = stock
        override fun findAll(): List<Stock> = emptyList()
        override fun findByProductId(productId: Int): Stock? =
            if (productId == 42) existingStock else null
        override fun findByProductIdWithLock(productId: Int): Stock? =
            if (productId == 42) existingStock else null
    }

    private val service = UpdateStockQuantityService(fakeRepository)

    @Test
    fun `should update stock quantity and return updated stock`() {
        val result = service.execute(42, 80)

        assertEquals("stock-1", result.id)
        assertEquals(42, result.productId)
        assertEquals(80, result.quantityAvailable)
    }

    @Test
    fun `should throw NoSuchElementException when stock not found`() {
        val exception = assertThrows<NoSuchElementException> {
            service.execute(999, 50)
        }

        assertEquals("Stock not found for product: 999", exception.message)
    }
}
