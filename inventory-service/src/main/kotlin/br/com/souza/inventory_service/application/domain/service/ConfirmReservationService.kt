package br.com.souza.inventory_service.application.domain.service

import br.com.souza.inventory_service.application.domain.model.ConfirmReservationCommand
import br.com.souza.inventory_service.application.domain.model.ReservationStatus
import br.com.souza.inventory_service.application.ports.`in`.ConfirmReservationUseCase
import br.com.souza.inventory_service.application.ports.out.StockReservationRepositoryPort
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ConfirmReservationService(
    private val reservationRepository: StockReservationRepositoryPort
) : ConfirmReservationUseCase {

    private val logger = LoggerFactory.getLogger(ConfirmReservationService::class.java)

    @Transactional
    override fun execute(command: ConfirmReservationCommand) {
        logger.info("Processing reservation confirmation: orderId={}", command.orderId)

        val reservation = reservationRepository.findByOrderId(command.orderId)
        if (reservation == null) {
            logger.warn("Reservation not found for orderId={}, skipping confirmation", command.orderId)
            return
        }

        val updatedReservation = reservation.copy(status = ReservationStatus.CONFIRMED)
        reservationRepository.save(updatedReservation)
        logger.info("Reservation confirmed: orderId={}, reservationId={}", command.orderId, reservation.id)
    }
}
