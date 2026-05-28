package br.com.souza.inventory_service.adapter.out.reservation.repository

import br.com.souza.inventory_service.adapter.out.reservation.models.StockReservationJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface StockReservationJpaRepository : JpaRepository<StockReservationJpaEntity, String>{
    fun findByOrderId(orderId: String): StockReservationJpaEntity?
}