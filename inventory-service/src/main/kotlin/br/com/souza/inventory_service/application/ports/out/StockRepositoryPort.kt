package br.com.souza.inventory_service.application.ports.out

import br.com.souza.inventory_service.application.domain.model.Stock

interface StockRepositoryPort {
    fun save(stock: Stock): Stock
    fun findAll(): List<Stock>
    fun findByProductId(productId: Int): Stock?
    fun findByProductIdWithLock(productId: Int): Stock?
}
