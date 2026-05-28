package br.com.souza.inventory_service.application.domain.model

enum class OutboxEventStatus {
    PENDING,
    PROCESSING,
    SENT,
    FAILED,
    DEAD_LETTER
}
