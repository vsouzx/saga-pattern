package br.com.souza.inventory_service.application.ports.out

import br.com.souza.inventory_service.application.domain.model.StockReservation

interface StockReservationRepositoryPort {
    fun save(reservation: StockReservation): StockReservation
    fun findByOrderId(orderId: String): StockReservation?
}
