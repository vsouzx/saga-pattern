package br.com.souza.inventory_service.application.ports.`in`

import br.com.souza.inventory_service.application.domain.model.Stock

interface ListStocksUseCase {
    fun execute(): List<Stock>
}
