package br.com.souza.inventory_service.adapter.out.stock.mappers

import br.com.souza.inventory_service.adapter.out.stock.models.StockJpaEntity
import br.com.souza.inventory_service.application.domain.model.Stock

fun StockJpaEntity.toDomain() = Stock(id, productId, quantityAvailable, updatedAt)

fun Stock.toJpaEntity() = StockJpaEntity(id ?: "", productId, quantityAvailable, updatedAt)
