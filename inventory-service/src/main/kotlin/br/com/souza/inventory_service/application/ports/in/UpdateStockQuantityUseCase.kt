package br.com.souza.inventory_service.application.ports.`in`

import br.com.souza.inventory_service.application.domain.model.Stock

interface UpdateStockQuantityUseCase {
    fun execute(productId: Int, quantityAvailable: Int): Stock
}
