package br.com.souza.inventory_service.application.domain.model

import java.time.LocalDateTime

data class Stock(
    val id: String? = null,
    val productId: Int,
    val quantityAvailable: Int,
    val updatedAt: LocalDateTime
)
