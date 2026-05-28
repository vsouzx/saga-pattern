package br.com.souza.inventory_service.adapter.out.product

import br.com.souza.inventory_service.adapter.out.product.mappers.toDomain
import br.com.souza.inventory_service.adapter.out.product.mappers.toJpaEntity
import br.com.souza.inventory_service.adapter.out.product.repository.ProductJpaRepository
import br.com.souza.inventory_service.adapter.out.stock.mappers.toDomain
import br.com.souza.inventory_service.adapter.out.stock.mappers.toJpaEntity
import br.com.souza.inventory_service.application.domain.model.Product
import br.com.souza.inventory_service.application.ports.out.ProductRepositoryPort
import org.springframework.stereotype.Component

@Component
class ProductRepositoryAdapter(
    private val jpaRepository: ProductJpaRepository
) : ProductRepositoryPort {

    override fun save(product: Product): Product =
        jpaRepository.save(product.toJpaEntity()).toDomain()

    override fun findAll(): List<Product> =
        jpaRepository.findAll().map { it.toDomain() }

    override fun findById(id: Int): Product? =
        jpaRepository.findById(id).orElse(null)?.toDomain()
}