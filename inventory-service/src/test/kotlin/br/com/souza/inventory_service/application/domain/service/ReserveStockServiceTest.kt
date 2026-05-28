package br.com.souza.inventory_service.application.domain.service

import br.com.souza.inventory_service.application.domain.model.*
import br.com.souza.inventory_service.application.ports.out.OutboxEventRepositoryPort
import br.com.souza.inventory_service.application.ports.out.ProductRepositoryPort
import br.com.souza.inventory_service.application.ports.out.StockRepositoryPort
import br.com.souza.inventory_service.application.ports.out.StockReservationRepositoryPort
import org.mockito.kotlin.*
import java.time.LocalDateTime
import kotlin.test.Test

class ReserveStockServiceTest {

    private val productRepository = mock<ProductRepositoryPort>()
    private val stockRepository = mock<StockRepositoryPort>()
    private val reservationRepository = mock<StockReservationRepositoryPort>()
    private val outboxRepository = mock<OutboxEventRepositoryPort>()

    private val service = ReserveStockService(
        productRepository,
        stockRepository,
        reservationRepository,
        outboxRepository
    )

    private val command = ReserveStockCommand(
        orderId = "order-1",
        productId = 1,
        quantity = 2,
        paymentType = "PIX",
        createdAt = "2026-05-23T20:54:29.702179-03:00",
        traceParent = "00-trace-span-01"
    )

    @Test
    fun `should reserve stock when product exists and stock is sufficient`() {
        val product = Product(id = 1, name = "Widget", price = 1000)
        val stock = Stock(id = "stock-1", productId = 1, quantityAvailable = 10, updatedAt = LocalDateTime.now())

        whenever(productRepository.findById(1)).thenReturn(product)
        whenever(stockRepository.findByProductIdWithLock(1)).thenReturn(stock)
        whenever(stockRepository.save(any())).thenAnswer { it.arguments[0] as Stock }
        whenever(reservationRepository.save(any())).thenAnswer { it.arguments[0] as StockReservation }
        whenever(outboxRepository.save(any())).thenAnswer { it.arguments[0] as OutboxEvent }

        service.execute(command)

        verify(stockRepository).save(argThat { quantityAvailable == 8 })
        verify(reservationRepository).save(argThat {
            orderId == "order-1" && productId == 1 && quantity == 2 && status == ReservationStatus.RESERVED
        })
        verify(outboxRepository).save(argThat {
            topic == "inventory.reserved" &&
            aggregateId == "order-1" &&
            aggregateType == "ORDER" &&
            eventType == "INVENTORY_RESERVED" &&
            traceParent == "00-trace-span-01"
        })
    }

    @Test
    fun `should publish insufficient stock event when product not found`() {
        whenever(productRepository.findById(1)).thenReturn(null)
        whenever(outboxRepository.save(any())).thenAnswer { it.arguments[0] as OutboxEvent }

        service.execute(command)

        verify(stockRepository, never()).findByProductIdWithLock(any())
        verify(outboxRepository).save(argThat {
            topic == "inventory.insufficient-stock" &&
            eventType == "INVENTORY_INSUFFICIENT_STOCK" &&
            payload.contains("PRODUCT_NOT_FOUND")
        })
    }

    @Test
    fun `should publish insufficient stock event when stock not found`() {
        val product = Product(id = 1, name = "Widget", price = 1000)
        whenever(productRepository.findById(1)).thenReturn(product)
        whenever(stockRepository.findByProductIdWithLock(1)).thenReturn(null)
        whenever(outboxRepository.save(any())).thenAnswer { it.arguments[0] as OutboxEvent }

        service.execute(command)

        verify(outboxRepository).save(argThat {
            topic == "inventory.insufficient-stock" &&
            payload.contains("STOCK_NOT_FOUND")
        })
    }

    @Test
    fun `should publish insufficient stock event when quantity is not enough`() {
        val product = Product(id = 1, name = "Widget", price = 1000)
        val stock = Stock(id = "stock-1", productId = 1, quantityAvailable = 1, updatedAt = LocalDateTime.now())

        whenever(productRepository.findById(1)).thenReturn(product)
        whenever(stockRepository.findByProductIdWithLock(1)).thenReturn(stock)
        whenever(outboxRepository.save(any())).thenAnswer { it.arguments[0] as OutboxEvent }

        service.execute(command)

        verify(stockRepository, never()).save(any())
        verify(outboxRepository).save(argThat {
            topic == "inventory.insufficient-stock" &&
            payload.contains("INSUFFICIENT_STOCK")
        })
    }

    @Test
    fun `should skip processing when event already processed for orderId`() {
        whenever(outboxRepository.existsByAggregateIdAndAggregateType(eq("order-1"), any(), any())).thenReturn(true)

        service.execute(command)

        verify(productRepository, never()).findById(any())
        verify(stockRepository, never()).findByProductIdWithLock(any())
        verify(reservationRepository, never()).save(any())
        verify(outboxRepository, never()).save(any())
    }

    @Test
    fun `should calculate amount as price times quantity`() {
        val product = Product(id = 1, name = "Widget", price = 1500)
        val stock = Stock(id = "stock-1", productId = 1, quantityAvailable = 10, updatedAt = LocalDateTime.now())

        whenever(productRepository.findById(1)).thenReturn(product)
        whenever(stockRepository.findByProductIdWithLock(1)).thenReturn(stock)
        whenever(stockRepository.save(any())).thenAnswer { it.arguments[0] as Stock }
        whenever(reservationRepository.save(any())).thenAnswer { it.arguments[0] as StockReservation }
        whenever(outboxRepository.save(any())).thenAnswer { it.arguments[0] as OutboxEvent }

        service.execute(command)

        verify(outboxRepository).save(argThat {
            payload.contains("3000")
        })
    }
}
