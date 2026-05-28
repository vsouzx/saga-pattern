package br.com.souza.inventory_service.application.ports.out

import br.com.souza.inventory_service.application.domain.model.Product

interface ProductRepositoryPort {
    fun save(product: Product): Product
    fun findAll(): List<Product>
    fun findById(id: Int): Product?
}
