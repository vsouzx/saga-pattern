package br.com.souza.inventory_service.adapter.`in`.web

import br.com.souza.inventory_service.application.domain.model.Product
import br.com.souza.inventory_service.application.domain.model.Stock
import br.com.souza.inventory_service.application.ports.`in`.CreateProductUseCase
import br.com.souza.inventory_service.application.ports.`in`.CreateStockUseCase
import br.com.souza.inventory_service.application.ports.`in`.ListProductsUseCase
import br.com.souza.inventory_service.application.ports.`in`.ListStocksUseCase
import br.com.souza.inventory_service.application.ports.`in`.UpdateStockQuantityUseCase
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@WebMvcTest(ProductController::class)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ProductControllerTest {

    @TestConfiguration
    class Config {
        private val products = mutableListOf<Product>()
        private val stocks = mutableListOf<Stock>()
        private var nextProductId = 1
        private var nextStockId = 1
        private val stockIdPrefix = "stock-"

        @Bean
        fun createProductUseCase() = object : CreateProductUseCase {
            override fun execute(name: String, price: Int): Product {
                val product = Product(id = nextProductId++, name = name, price = price)
                products.add(product)
                return product
            }
        }

        @Bean
        fun listProductsUseCase() = object : ListProductsUseCase {
            override fun execute(): List<Product> = products.toList()
        }

        @Bean
        fun createStockUseCase() = object : CreateStockUseCase {
            override fun execute(productId: Int, quantityAvailable: Int): Stock {
                val stock = Stock(
                    id = "$stockIdPrefix${nextStockId++}",
                    productId = productId,
                    quantityAvailable = quantityAvailable,
                    updatedAt = LocalDateTime.of(2026, 5, 23, 10, 0)
                )
                stocks.add(stock)
                return stock
            }
        }

        @Bean
        fun listStocksUseCase() = object : ListStocksUseCase {
            override fun execute(): List<Stock> = stocks.toList()
        }

        @Bean
        fun updateStockQuantityUseCase() = object : UpdateStockQuantityUseCase {
            override fun execute(productId: Int, quantityAvailable: Int): Stock {
                val stock = stocks.find { it.productId == productId }
                    ?: throw NoSuchElementException("Stock not found for product: $productId")
                val updated = stock.copy(
                    quantityAvailable = quantityAvailable,
                    updatedAt = LocalDateTime.of(2026, 5, 23, 11, 0)
                )
                stocks[stocks.indexOf(stock)] = updated
                return updated
            }
        }
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @Order(1)
    fun `POST products should create product and return 201`() {
        mockMvc.perform(
            post("/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name": "Notebook", "price": 299990}""")
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Notebook"))
            .andExpect(jsonPath("$.price").value(299990))
    }

    @Test
    @Order(2)
    fun `GET products should return list and 200`() {
        mockMvc.perform(get("/v1/products"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    @Order(3)
    fun `POST products productId stock should create stock and return 201`() {
        mockMvc.perform(
            post("/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name": "Mouse", "price": 4990}""")
        )

        mockMvc.perform(
            post("/v1/products/2/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"quantityAvailable": 100}""")
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.productId").value(2))
            .andExpect(jsonPath("$.quantityAvailable").value(100))
    }

    @Test
    @Order(4)
    fun `GET products stocks should return list and 200`() {
        mockMvc.perform(get("/v1/products/stocks"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    @Order(5)
    fun `PATCH products productId stock quantity should update and return 200`() {
        mockMvc.perform(
            patch("/v1/products/2/stock/quantity")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"quantityAvailable": 80}""")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.quantityAvailable").value(80))
            .andExpect(jsonPath("$.productId").value(2))
    }
}
