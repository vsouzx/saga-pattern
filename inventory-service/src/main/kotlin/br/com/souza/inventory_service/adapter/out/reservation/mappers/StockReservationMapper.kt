package br.com.souza.inventory_service.adapter.out.reservation.mappers

import br.com.souza.inventory_service.adapter.out.reservation.models.StockReservationJpaEntity
import br.com.souza.inventory_service.application.domain.model.ReservationStatus
import br.com.souza.inventory_service.application.domain.model.StockReservation

fun StockReservation.toJpaEntity() = StockReservationJpaEntity(
    id = id ?: "",
    orderId = orderId,
    productId = productId,
    quantity = quantity,
    status = status.name,
    createdAt = createdAt
)

fun StockReservationJpaEntity.toDomain(): StockReservation = StockReservation(
    id = id,
    orderId = orderId,
    productId = productId,
    quantity = quantity,
    status = ReservationStatus.valueOf(status),
    createdAt = createdAt
)
