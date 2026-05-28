package br.com.souza.inventory_service.adapter.out.stock.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "stocks")
data class StockJpaEntity(
    @Id
    val id: String = "",

    @Column(name = "product_id", unique = true, nullable = false)
    val productId: Int = 0,

    @Column(name = "quantity_available", nullable = false)
    val quantityAvailable: Int = 0,

    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)