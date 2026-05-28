package br.com.souza.inventory_service.adapter.`in`.web

import br.com.souza.inventory_service.adapter.`in`.web.dto.CreateProductRequest
import br.com.souza.inventory_service.adapter.`in`.web.dto.CreateStockRequest
import br.com.souza.inventory_service.adapter.`in`.web.dto.ProductResponse
import br.com.souza.inventory_service.adapter.`in`.web.dto.StockResponse
import br.com.souza.inventory_service.adapter.`in`.web.dto.UpdateStockQuantityRequest
import br.com.souza.inventory_service.application.ports.`in`.CreateProductUseCase
import br.com.souza.inventory_service.application.ports.`in`.CreateStockUseCase
import br.com.souza.inventory_service.application.ports.`in`.ListProductsUseCase
import br.com.souza.inventory_service.application.ports.`in`.ListStocksUseCase
import br.com.souza.inventory_service.application.ports.`in`.UpdateStockQuantityUseCase
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/products")
class ProductController(
    private val createProduct: CreateProductUseCase,
    private val listProducts: ListProductsUseCase,
    private val createStock: CreateStockUseCase,
    private val listStocks: ListStocksUseCase,
    private val updateStockQuantity: UpdateStockQuantityUseCase
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun create(@RequestBody request: CreateProductRequest): ProductResponse {
        val product = createProduct.execute(request.name, request.price)
        return ProductResponse.from(product)
    }

    @GetMapping
    fun list(): List<ProductResponse> {
        return listProducts.execute().map { ProductResponse.from(it) }
    }

    @GetMapping("/stocks")
    fun listStocks(): List<StockResponse> {
        return listStocks.execute().map { StockResponse.from(it) }
    }

    @PostMapping("/{productId}/stock")
    @ResponseStatus(HttpStatus.CREATED)
    fun createStock(
        @PathVariable productId: Int,
        @RequestBody request: CreateStockRequest
    ): StockResponse {
        val stock = createStock.execute(productId, request.quantityAvailable)
        return StockResponse.from(stock)
    }

    @PatchMapping("/{productId}/stock/quantity")
    fun updateStockQuantity(
        @PathVariable productId: Int,
        @RequestBody request: UpdateStockQuantityRequest
    ): StockResponse {
        val stock = updateStockQuantity.execute(productId, request.quantityAvailable)
        return StockResponse.from(stock)
    }
}
