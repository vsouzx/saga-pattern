package br.com.souza.inventory_service.application.domain.service

import br.com.souza.inventory_service.application.domain.model.Stock
import br.com.souza.inventory_service.application.ports.`in`.ListStocksUseCase
import br.com.souza.inventory_service.application.ports.out.StockRepositoryPort
import org.springframework.stereotype.Service

@Service
class ListStocksService(
    private val stockRepository: StockRepositoryPort
) : ListStocksUseCase {

    override fun execute(): List<Stock> = stockRepository.findAll()
}
