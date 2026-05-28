package br.com.souza.inventory_service.adapter.out.relay.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "outbox_events")
data class OutboxEventJpaEntity(
    @Id
    val id: String = "",

    @Column(name = "aggregate_id", nullable = false)
    val aggregateId: String = "",

    @Column(name = "aggregate_type", nullable = false)
    val aggregateType: String = "",

    @Column(name = "event_type", nullable = false)
    val eventType: String = "",

    @Column(nullable = false)
    val topic: String = "",

    @Column(nullable = false, columnDefinition = "TEXT")
    val payload: String = "",

    @Column(name = "trace_parent")
    val traceParent: String? = null,

    @Column(nullable = false)
    val status: String = "PENDING",

    @Column(name = "retries_count", nullable = false)
    val retriesCount: Int = 0,

    @Column(name = "max_retries", nullable = false)
    val maxRetries: Int = 5,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "sent_at")
    val sentAt: LocalDateTime? = null,

    @Column(name = "locked_at")
    val lockedAt: LocalDateTime? = null
)
