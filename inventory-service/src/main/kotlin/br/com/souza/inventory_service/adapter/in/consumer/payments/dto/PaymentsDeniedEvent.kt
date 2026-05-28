package br.com.souza.inventory_service.adapter.`in`.consumer.payments.dto

data class PaymentsDeniedEvent (
    val paymentId: String = "",
    val orderId: String = "",
    val status: String = "",
    val reason: String = "",
    val createdAt: String = ""
)