package br.com.souza.inventory_service.application.domain.service

import br.com.souza.inventory_service.application.domain.model.Product
import br.com.souza.inventory_service.application.ports.out.ProductRepositoryPort
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CreateProductServiceTest {

    private val savedProducts = mutableListOf<Product>()

    private val fakeRepository = object : ProductRepositoryPort {
        override fun save(product: Product): Product {
            val saved = product.copy(id = 1)
            savedProducts.add(saved)
            return saved
        }
        override fun findAll(): List<Product> = emptyList()
        override fun findById(id: Int): Product? = null
    }

    private val service = CreateProductService(fakeRepository)

    @Test
    fun `should create product and return saved product with id`() {
        val result = service.execute("Notebook", 299990)

        assertEquals(1, result.id)
        assertEquals("Notebook", result.name)
        assertEquals(299990, result.price)
    }

    @Test
    fun `should pass product without id to repository`() {
        service.execute("Mouse", 4990)

        val saved = savedProducts.last()
        assertEquals("Mouse", saved.name)
        assertEquals(4990, saved.price)
    }
}
