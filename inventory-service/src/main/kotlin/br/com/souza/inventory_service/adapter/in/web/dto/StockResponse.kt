package br.com.souza.inventory_service.adapter.`in`.web.dto

import br.com.souza.inventory_service.application.domain.model.Stock
import java.time.LocalDateTime

data class StockResponse(
    val id: String,
    val productId: Int,
    val quantityAvailable: Int,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(stock: Stock) = StockResponse(
            id = stock.id!!,
            productId = stock.productId,
            quantityAvailable = stock.quantityAvailable,
            updatedAt = stock.updatedAt
        )
    }
}
