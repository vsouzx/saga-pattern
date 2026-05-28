package br.com.souza.inventory_service.application.domain.service

import br.com.souza.inventory_service.application.domain.model.*
import br.com.souza.inventory_service.application.ports.`in`.ReserveStockUseCase
import br.com.souza.inventory_service.application.ports.out.OutboxEventRepositoryPort
import br.com.souza.inventory_service.application.ports.out.ProductRepositoryPort
import br.com.souza.inventory_service.application.ports.out.StockRepositoryPort
import br.com.souza.inventory_service.application.ports.out.StockReservationRepositoryPort
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class ReserveStockService(
    private val productRepository: ProductRepositoryPort,
    private val stockRepository: StockRepositoryPort,
    private val reservationRepository: StockReservationRepositoryPort,
    private val outboxRepository: OutboxEventRepositoryPort
) : ReserveStockUseCase {

    private final val logger = LoggerFactory.getLogger(ReserveStockService::class.java)
    private final val objectMapper = ObjectMapper()
    private final val aggregateType = "ORDER"
    private final val failureEventType = "INVENTORY_INSUFFICIENT_STOCK"
    private final val successEventType = "INVENTORY_RESERVED"

    @Transactional
    override fun execute(command: ReserveStockCommand) {
        val aggregateId = command.orderId

        if (outboxRepository.existsByAggregateIdAndAggregateType(aggregateId, aggregateType, listOf(failureEventType, successEventType))) {
            logger.info("Event already processed, skipping: orderId={}", command.orderId)
            return
        }

        logger.info("Processing stock reservation: orderId={}, productId={}, quantity={}",
            command.orderId, command.productId, command.quantity)

        val product = productRepository.findById(command.productId)
        if (product == null) {
            logger.warn("Product not found: productId={}, orderId={}",
                command.productId, command.orderId)
            saveFailureEvent(command, "PRODUCT_NOT_FOUND")
            return
        }

        val stock = stockRepository.findByProductIdWithLock(command.productId)
        if (stock == null) {
            logger.warn("Stock not found: productId={}, orderId={}",
                command.productId, command.orderId)
            saveFailureEvent(command, "STOCK_NOT_FOUND")
            return
        }

        if (stock.quantityAvailable < command.quantity) {
            logger.warn("Insufficient stock: available={}, requested={}, productId={}, orderId={}",
                stock.quantityAvailable, command.quantity, command.productId, command.orderId)
            saveFailureEvent(command, "INSUFFICIENT_STOCK")
            return
        }

        val updatedStock = stock.copy(
            quantityAvailable = stock.quantityAvailable - command.quantity,
            updatedAt = LocalDateTime.now()
        )
        stockRepository.save(updatedStock)
        logger.info("Stock decremented: productId={}, previousQty={}, newQty={}",
            command.productId, stock.quantityAvailable, updatedStock.quantityAvailable)

        val reservation = StockReservation(
            id = UUID.randomUUID().toString(),
            orderId = command.orderId,
            productId = command.productId,
            quantity = command.quantity,
            status = ReservationStatus.RESERVED,
            createdAt = LocalDateTime.now()
        )
        reservationRepository.save(reservation)
        logger.info("Stock reservation created: reservationId={}, orderId={}",
            reservation.id, command.orderId)

        val amount = product.price * command.quantity
        val payload = objectMapper.writeValueAsString(
            mapOf(
                "orderId" to command.orderId,
                "amount" to amount,
                "paymentType" to command.paymentType,
                "createdAt" to command.createdAt
            )
        )

        val outboxEvent = OutboxEvent(
            id = UUID.randomUUID().toString(),
            aggregateId = command.orderId,
            aggregateType = aggregateType,
            eventType = successEventType,
            topic = "inventory.reserved",
            payload = payload,
            traceParent = command.traceParent
        )
        outboxRepository.save(outboxEvent)
        logger.info("Outbox event saved: topic=inventory.reserved, orderId={}, amount={}",
            command.orderId, amount)
    }

    private fun saveFailureEvent(command: ReserveStockCommand, reason: String) {
        val payload = objectMapper.writeValueAsString(
            mapOf(
                "orderId" to command.orderId,
                "reason" to reason,
                "createdAt" to command.createdAt
            )
        )

        val outboxEvent = OutboxEvent(
            id = UUID.randomUUID().toString(),
            aggregateId = command.orderId,
            aggregateType = aggregateType,
            eventType = failureEventType,
            topic = "inventory.insufficient-stock",
            payload = payload,
            traceParent = command.traceParent
        )
        outboxRepository.save(outboxEvent)
        logger.info("Outbox failure event saved: topic=inventory.insufficient-stock, orderId={}, reason={}",
            command.orderId, reason)
    }
}
