package br.com.souza.inventory_service.adapter.`in`.web.dto

import br.com.souza.inventory_service.application.domain.model.Product

data class ProductResponse(
    val id: Int,
    val name: String,
    val price: Int
) {
    companion object {
        fun from(product: Product) = ProductResponse(
            id = product.id!!,
            name = product.name,
            price = product.price
        )
    }
}
