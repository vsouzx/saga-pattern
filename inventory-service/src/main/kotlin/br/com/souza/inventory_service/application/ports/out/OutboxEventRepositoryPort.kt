package br.com.souza.inventory_service.application.ports.out

import br.com.souza.inventory_service.application.domain.model.OutboxEvent

interface OutboxEventRepositoryPort {
    fun save(event: OutboxEvent): OutboxEvent
    fun existsByAggregateIdAndAggregateType(aggregateId: String, aggregateType: String, eventTypes: List<String>): Boolean
    fun findPendingEvents(limit: Int): List<OutboxEvent>
    fun lockEvents(ids: List<String>): Int
    fun markAsSent(id: String)
    fun markAsFailed(id: String)
    fun markAsDeadLetter(id: String)
}
