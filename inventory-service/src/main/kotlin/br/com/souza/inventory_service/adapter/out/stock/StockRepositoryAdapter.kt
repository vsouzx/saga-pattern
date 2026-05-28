package br.com.souza.inventory_service.adapter.out.stock

import br.com.souza.inventory_service.adapter.out.stock.repository.StockJpaRepository
import br.com.souza.inventory_service.adapter.out.stock.mappers.toDomain
import br.com.souza.inventory_service.adapter.out.stock.mappers.toJpaEntity
import br.com.souza.inventory_service.application.domain.model.Stock
import br.com.souza.inventory_service.application.ports.out.StockRepositoryPort
import org.springframework.stereotype.Component

@Component
class StockRepositoryAdapter(
    private val jpaRepository: StockJpaRepository
) : StockRepositoryPort {

    override fun save(stock: Stock): Stock =
        jpaRepository.save(stock.toJpaEntity()).toDomain()

    override fun findAll(): List<Stock> =
        jpaRepository.findAll().map { it.toDomain() }

    override fun findByProductId(productId: Int): Stock? =
        jpaRepository.findByProductId(productId)?.toDomain()

    override fun findByProductIdWithLock(productId: Int): Stock? =
        jpaRepository.findByProductIdWithLock(productId)?.toDomain()
}