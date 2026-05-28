package br.com.souza.inventory_service.adapter.out.reservation

import br.com.souza.inventory_service.adapter.out.reservation.mappers.toDomain
import br.com.souza.inventory_service.adapter.out.reservation.mappers.toJpaEntity
import br.com.souza.inventory_service.adapter.out.reservation.repository.StockReservationJpaRepository
import br.com.souza.inventory_service.application.domain.model.StockReservation
import br.com.souza.inventory_service.application.ports.out.StockReservationRepositoryPort
import org.springframework.stereotype.Component

@Component
class StockReservationRepositoryAdapter(
    private val jpaRepository: StockReservationJpaRepository
) : StockReservationRepositoryPort {

    override fun save(reservation: StockReservation): StockReservation =
        jpaRepository.save(reservation.toJpaEntity()).toDomain()

    override fun findByOrderId(orderId: String): StockReservation? =
        jpaRepository.findByOrderId(orderId)?.toDomain()
}