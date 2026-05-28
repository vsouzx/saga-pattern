package br.com.souza.inventory_service.adapter.`in`.consumer.order.dto

data class OrderConfirmedEvent(
    val status: String = "",
    val orderId: String = "",
    val timestamp: String = ""
)
