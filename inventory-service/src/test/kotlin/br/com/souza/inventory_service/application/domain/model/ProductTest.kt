package br.com.souza.inventory_service.application.domain.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ProductTest {

    @Test
    fun `should create product with all fields`() {
        val product = Product(
            id = 1,
            name = "Notebook",
            price = 299990
        )

        assertEquals(1, product.id)
        assertEquals("Notebook", product.name)
        assertEquals(299990, product.price)
    }

    @Test
    fun `should create product with null id by default`() {
        val product = Product(
            name = "Mouse",
            price = 4990
        )

        assertNull(product.id)
    }
}
