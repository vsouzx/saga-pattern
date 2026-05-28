package br.com.souza.inventory_service.adapter.out.product.mappers

import br.com.souza.inventory_service.adapter.out.product.models.ProductJpaEntity
import br.com.souza.inventory_service.application.domain.model.Product

fun ProductJpaEntity.toDomain() = Product(id, name, price)

fun Product.toJpaEntity() = ProductJpaEntity(id ?: 0, name, price)
