package br.com.souza.inventory_service.adapter.out.reservation.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "stock_reservations")
data class StockReservationJpaEntity(
    @Id
    val id: String = "",

    @Column(name = "order_id", nullable = false)
    val orderId: String = "",

    @Column(name = "product_id", nullable = false)
    val productId: Int = 0,

    @Column(nullable = false)
    val quantity: Int = 0,

    @Column(nullable = false)
    val status: String = "RESERVED",

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)