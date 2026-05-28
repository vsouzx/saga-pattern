package br.com.souza.inventory_service.adapter.out.stock.repository

import br.com.souza.inventory_service.adapter.out.stock.models.StockJpaEntity
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface StockJpaRepository : JpaRepository<StockJpaEntity, String> {
    fun findByProductId(productId: Int): StockJpaEntity?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM StockJpaEntity s WHERE s.productId = :productId")
    fun findByProductIdWithLock(@Param("productId") productId: Int): StockJpaEntity?
}
