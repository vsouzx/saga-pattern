package br.com.souza.inventory_service.application.domain.service

import br.com.souza.inventory_service.application.domain.model.Stock
import br.com.souza.inventory_service.application.ports.out.StockRepositoryPort
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ListStocksServiceTest {

    private val stocks = mutableListOf<Stock>()

    private val fakeRepository = object : StockRepositoryPort {
        override fun save(stock: Stock): Stock = stock
        override fun findAll(): List<Stock> = stocks.toList()
        override fun findByProductId(productId: Int): Stock? = null
        override fun findByProductIdWithLock(productId: Int): Stock? = null
    }

    private val service = ListStocksService(fakeRepository)

    @Test
    fun `should return empty list when no stocks exist`() {
        val result = service.execute()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `should return all stocks`() {
        val now = LocalDateTime.of(2026, 5, 23, 10, 0)
        stocks.add(Stock(id = "s1", productId = 1, quantityAvailable = 100, updatedAt = now))
        stocks.add(Stock(id = "s2", productId = 2, quantityAvailable = 50, updatedAt = now))

        val result = service.execute()

        assertEquals(2, result.size)
        assertEquals(1, result[0].productId)
        assertEquals(2, result[1].productId)
    }
}
