package br.com.souza.inventory_service.application.domain.service

import br.com.souza.inventory_service.application.domain.model.Product
import br.com.souza.inventory_service.application.ports.`in`.ListProductsUseCase
import br.com.souza.inventory_service.application.ports.out.ProductRepositoryPort
import org.springframework.stereotype.Service

@Service
class ListProductsService(
    private val productRepository: ProductRepositoryPort
) : ListProductsUseCase {

    override fun execute(): List<Product> = productRepository.findAll()
}
