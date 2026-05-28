package br.com.souza.inventory_service.application.ports.`in`

import br.com.souza.inventory_service.application.domain.model.ConfirmReservationCommand

interface ConfirmReservationUseCase {
    fun execute(command: ConfirmReservationCommand)
}
