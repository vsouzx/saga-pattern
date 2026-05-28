package br.com.souza.inventory_service.application.ports.`in`

import br.com.souza.inventory_service.application.domain.model.Product

interface ListProductsUseCase {
    fun execute(): List<Product>
}
