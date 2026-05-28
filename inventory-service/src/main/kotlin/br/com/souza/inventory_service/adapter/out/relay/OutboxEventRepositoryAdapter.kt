package br.com.souza.inventory_service.adapter.out.relay

import br.com.souza.inventory_service.adapter.out.relay.mappers.toDomain
import br.com.souza.inventory_service.adapter.out.relay.mappers.toJpaEntity
import br.com.souza.inventory_service.adapter.out.relay.repository.OutboxEventJpaRepository
import br.com.souza.inventory_service.adapter.out.stock.mappers.toDomain
import br.com.souza.inventory_service.adapter.out.stock.mappers.toJpaEntity
import br.com.souza.inventory_service.application.domain.model.OutboxEvent
import br.com.souza.inventory_service.application.ports.out.OutboxEventRepositoryPort
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class OutboxEventRepositoryAdapter(
    private val jpaRepository: OutboxEventJpaRepository
) : OutboxEventRepositoryPort {

    override fun save(event: OutboxEvent): OutboxEvent =
        jpaRepository.save(event.toJpaEntity()).toDomain()

    override fun existsByAggregateIdAndAggregateType(aggregateId: String, aggregateType: String, eventTypes: List<String>): Boolean =
        jpaRepository.existsByAggregateIdAndAggregateTypeAndEventTypeIn(aggregateId, aggregateType, eventTypes )

    override fun findPendingEvents(limit: Int): List<OutboxEvent> {
        val expiredBefore = LocalDateTime.now().minusMinutes(5)
        return jpaRepository.findPendingEvents(limit, expiredBefore).map { it.toDomain() }
    }

    override fun lockEvents(ids: List<String>): Int =
        jpaRepository.lockEvents(ids, LocalDateTime.now())

    override fun markAsSent(id: String) =
        jpaRepository.markAsSent(id, LocalDateTime.now())

    override fun markAsFailed(id: String) =
        jpaRepository.markAsFailed(id)

    override fun markAsDeadLetter(id: String) =
        jpaRepository.markAsDeadLetter(id)
}