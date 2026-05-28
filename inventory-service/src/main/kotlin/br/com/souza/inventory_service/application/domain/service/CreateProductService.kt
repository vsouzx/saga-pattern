package br.com.souza.inventory_service.application.domain.service

import br.com.souza.inventory_service.application.domain.model.Product
import br.com.souza.inventory_service.application.ports.`in`.CreateProductUseCase
import br.com.souza.inventory_service.application.ports.out.ProductRepositoryPort
import org.springframework.stereotype.Service

@Service
class CreateProductService(
    private val productRepository: ProductRepositoryPort
) : CreateProductUseCase {

    override fun execute(name: String, price: Int): Product {
        val product = Product(name = name, price = price)
        return productRepository.save(product)
    }
}
