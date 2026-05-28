package br.com.souza.inventory_service.application.domain.model

import java.time.LocalDateTime

data class OutboxEvent(
    val id: String? = null,
    val aggregateId: String,
    val aggregateType: String,
    val eventType: String,
    val topic: String,
    val payload: String,
    val traceParent: String?,
    val status: OutboxEventStatus = OutboxEventStatus.PENDING,
    val retriesCount: Int = 0,
    val maxRetries: Int = 5,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val sentAt: LocalDateTime? = null,
    val lockedAt: LocalDateTime? = null
)
