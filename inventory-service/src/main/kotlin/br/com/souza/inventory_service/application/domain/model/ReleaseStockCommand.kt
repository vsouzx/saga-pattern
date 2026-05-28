package br.com.souza.inventory_service.application.domain.model

data class ReleaseStockCommand(
    val orderId: String,
    val reason: String,
    val traceParent: String?
)
