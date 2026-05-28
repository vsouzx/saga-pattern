package br.com.souza.inventory_service.application.domain.service

import br.com.souza.inventory_service.application.domain.model.Product
import br.com.souza.inventory_service.application.ports.out.ProductRepositoryPort
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ListProductsServiceTest {

    private val products = mutableListOf<Product>()

    private val fakeRepository = object : ProductRepositoryPort {
        override fun save(product: Product): Product = product
        override fun findAll(): List<Product> = products.toList()
        override fun findById(id: Int): Product? = null
    }

    private val service = ListProductsService(fakeRepository)

    @Test
    fun `should return empty list when no products exist`() {
        val result = service.execute()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `should return all products`() {
        products.add(Product(id = 1, name = "Notebook", price = 299990))
        products.add(Product(id = 2, name = "Mouse", price = 4990))

        val result = service.execute()

        assertEquals(2, result.size)
        assertEquals("Notebook", result[0].name)
        assertEquals("Mouse", result[1].name)
    }
}
