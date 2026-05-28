package br.com.souza.inventory_service.adapter.out.relay.repository

import br.com.souza.inventory_service.adapter.out.relay.models.OutboxEventJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface OutboxEventJpaRepository : JpaRepository<OutboxEventJpaEntity, String> {

    fun existsByAggregateIdAndAggregateTypeAndEventTypeIn(aggregateId: String, aggregateType: String, eventTypes: List<String>): Boolean

    @Query("""
        SELECT e FROM OutboxEventJpaEntity e
        WHERE (e.status = 'PENDING' OR e.status = 'FAILED')
        AND (e.lockedAt IS NULL OR e.lockedAt < :expiredBefore)
        ORDER BY e.createdAt ASC
        LIMIT :limit
    """)
    fun findPendingEvents(
        @Param("limit") limit: Int,
        @Param("expiredBefore") expiredBefore: LocalDateTime
    ): List<OutboxEventJpaEntity>

    @Modifying
    @Query("""
        UPDATE OutboxEventJpaEntity e
        SET e.status = 'PROCESSING', e.lockedAt = :now
        WHERE e.id IN :ids AND (e.status = 'PENDING' OR e.status = 'FAILED')
    """)
    fun lockEvents(@Param("ids") ids: List<String>, @Param("now") now: LocalDateTime): Int

    @Modifying
    @Query("""
        UPDATE OutboxEventJpaEntity e
        SET e.status = 'SENT', e.sentAt = :now, e.lockedAt = NULL
        WHERE e.id = :id
    """)
    fun markAsSent(@Param("id") id: String, @Param("now") now: LocalDateTime)

    @Modifying
    @Query("""
        UPDATE OutboxEventJpaEntity e
        SET e.status = 'FAILED', e.lockedAt = NULL, e.retriesCount = e.retriesCount + 1
        WHERE e.id = :id
    """)
    fun markAsFailed(@Param("id") id: String)

    @Modifying
    @Query("""
        UPDATE OutboxEventJpaEntity e
        SET e.status = 'DEAD_LETTER', e.lockedAt = NULL
        WHERE e.id = :id
    """)
    fun markAsDeadLetter(@Param("id") id: String)
}