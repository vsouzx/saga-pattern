package br.com.souza.inventory_service.application.domain.service

import br.com.souza.inventory_service.application.domain.model.Stock
import br.com.souza.inventory_service.application.ports.`in`.UpdateStockQuantityUseCase
import br.com.souza.inventory_service.application.ports.out.StockRepositoryPort
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UpdateStockQuantityService(
    private val stockRepository: StockRepositoryPort
) : UpdateStockQuantityUseCase {

    override fun execute(productId: Int, quantityAvailable: Int): Stock {
        val stock = stockRepository.findByProductId(productId)
            ?: throw NoSuchElementException("Stock not found for product: $productId")
        return stockRepository.save(stock.copy(quantityAvailable = quantityAvailable, updatedAt = LocalDateTime.now()))
    }
}
