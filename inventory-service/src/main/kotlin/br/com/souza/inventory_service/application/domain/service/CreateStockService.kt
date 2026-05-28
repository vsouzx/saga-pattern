package br.com.souza.inventory_service.application.domain.service

import br.com.souza.inventory_service.application.domain.model.Stock
import br.com.souza.inventory_service.application.ports.`in`.CreateStockUseCase
import br.com.souza.inventory_service.application.ports.out.ProductRepositoryPort
import br.com.souza.inventory_service.application.ports.out.StockRepositoryPort
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class CreateStockService(
    private val stockRepository: StockRepositoryPort,
    private val productRepository: ProductRepositoryPort
) : CreateStockUseCase {

    override fun execute(productId: Int, quantityAvailable: Int): Stock {
        productRepository.findById(productId)
            ?: throw NoSuchElementException("Product not found: $productId")

        val stock = Stock(
            id = UUID.randomUUID().toString(),
            productId = productId,
            quantityAvailable = quantityAvailable,
            updatedAt = LocalDateTime.now()
        )
        return stockRepository.save(stock)
    }
}
