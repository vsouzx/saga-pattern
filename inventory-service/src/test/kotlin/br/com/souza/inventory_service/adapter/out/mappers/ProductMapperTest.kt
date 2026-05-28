package br.com.souza.inventory_service.adapter.out.mappers

import br.com.souza.inventory_service.adapter.out.product.models.ProductJpaEntity
import br.com.souza.inventory_service.adapter.out.product.mappers.toDomain
import br.com.souza.inventory_service.adapter.out.product.mappers.toJpaEntity
import br.com.souza.inventory_service.application.domain.model.Product
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ProductMapperTest {

    @Test
    fun `should convert JPA entity to domain model`() {
        val jpaEntity = ProductJpaEntity(
            id = 1,
            name = "Notebook",
            price = 299990
        )

        val domain = jpaEntity.toDomain()

        assertEquals(1, domain.id)
        assertEquals("Notebook", domain.name)
        assertEquals(299990, domain.price)
    }

    @Test
    fun `should convert domain model to JPA entity`() {
        val product = Product(
            id = 1,
            name = "Mouse",
            price = 4990
        )

        val jpaEntity = product.toJpaEntity()

        assertEquals(1, jpaEntity.id)
        assertEquals("Mouse", jpaEntity.name)
        assertEquals(4990, jpaEntity.price)
    }

    @Test
    fun `should convert domain model with null id to JPA entity with id 0`() {
        val product = Product(
            name = "Teclado",
            price = 19990
        )

        val jpaEntity = product.toJpaEntity()

        assertEquals(0, jpaEntity.id)
    }
}
