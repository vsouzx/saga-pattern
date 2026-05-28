package br.com.souza.inventory_service.application.domain.model

data class Product(
    val id: Int? = null,
    val name: String,
    val price: Int
)
