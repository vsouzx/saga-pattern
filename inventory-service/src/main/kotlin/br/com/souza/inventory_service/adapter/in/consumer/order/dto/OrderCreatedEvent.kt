package br.com.souza.inventory_service.adapter.`in`.consumer.order.dto

data class OrderCreatedEvent(
    val userId: String = "",
    val orderId: String = "",
    val quantity: Int = 0,
    val createdAt: String = "",
    val productId: Int = 0,
    val paymentType: String = ""
)
