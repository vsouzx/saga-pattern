package br.com.souza.inventory_service.application.ports.`in`

import br.com.souza.inventory_service.application.domain.model.ReleaseStockCommand

interface ReleaseStockUseCase {
    fun execute(command: ReleaseStockCommand)
}
