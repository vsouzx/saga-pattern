package br.com.souza.inventory_service.adapter.out.product.repository

import br.com.souza.inventory_service.adapter.out.product.models.ProductJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProductJpaRepository : JpaRepository<ProductJpaEntity, Int>