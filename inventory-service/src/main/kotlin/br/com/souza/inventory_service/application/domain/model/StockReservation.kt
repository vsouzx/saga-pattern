package br.com.souza.inventory_service.application.domain.model

import java.time.LocalDateTime

data class StockReservation(
    val id: String? = null,
    val orderId: String,
    val productId: Int,
    val quantity: Int,
    val status: ReservationStatus,
    val createdAt: LocalDateTime
)
