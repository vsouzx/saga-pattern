package br.com.souza.inventory_service.application.ports.`in`

import br.com.souza.inventory_service.application.domain.model.ReserveStockCommand

interface ReserveStockUseCase {
    fun execute(command: ReserveStockCommand)
}
