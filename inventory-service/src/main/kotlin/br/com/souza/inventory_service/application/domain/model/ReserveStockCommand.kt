package br.com.souza.inventory_service.application.domain.model

data class ReserveStockCommand(
    val orderId: String,
    val productId: Int,
    val quantity: Int,
    val paymentType: String,
    val createdAt: String,
    val traceParent: String?
)
