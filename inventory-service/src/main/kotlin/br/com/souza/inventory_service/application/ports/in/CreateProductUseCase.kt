package br.com.souza.inventory_service.application.ports.`in`

import br.com.souza.inventory_service.application.domain.model.Product

interface CreateProductUseCase {
    fun execute(name: String, price: Int): Product
}
