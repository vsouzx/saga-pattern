package br.com.souza.inventory_service.application.domain.service

import br.com.souza.inventory_service.application.domain.model.Product
import br.com.souza.inventory_service.application.domain.model.Stock
import br.com.souza.inventory_service.application.ports.out.ProductRepositoryPort
import br.com.souza.inventory_service.application.ports.out.StockRepositoryPort
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class CreateStockServiceTest {

    private val existingProduct = Product(id = 1, name = "Notebook", price = 299990)

    private val fakeProductRepository = object : ProductRepositoryPort {
        override fun save(product: Product): Product = product
        override fun findAll(): List<Product> = emptyList()
        override fun findById(id: Int): Product? =
            if (id == 1) existingProduct else null
    }

    private val fakeStockRepository = object : StockRepositoryPort {
        override fun save(stock: Stock): Stock = stock
        override fun findAll(): List<Stock> = emptyList()
        override fun findByProductId(productId: Int): Stock? = null
        override fun findByProductIdWithLock(productId: Int): Stock? = null
    }

    private val service = CreateStockService(fakeStockRepository, fakeProductRepository)

    @Test
    fun `should create stock for existing product`() {
        val result = service.execute(1, 100)

        assertEquals(1, result.productId)
        assertEquals(100, result.quantityAvailable)
    }

    @Test
    fun `should throw NoSuchElementException when product not found`() {
        val exception = assertThrows<NoSuchElementException> {
            service.execute(999, 100)
        }

        assertEquals("Product not found: 999", exception.message)
    }
}
