package br.com.souza.inventory_service.application.domain.service

import br.com.souza.inventory_service.application.domain.model.ConfirmReservationCommand
import br.com.souza.inventory_service.application.domain.model.ReservationStatus
import br.com.souza.inventory_service.application.domain.model.StockReservation
import br.com.souza.inventory_service.application.ports.out.StockReservationRepositoryPort
import org.mockito.kotlin.*
import java.time.LocalDateTime
import kotlin.test.Test

class ConfirmReservationServiceTest {

    private val reservationRepository = mock<StockReservationRepositoryPort>()

    private val service = ConfirmReservationService(reservationRepository)

    private val command = ConfirmReservationCommand(
        orderId = "order-1",
        traceParent = "00-trace-span-01"
    )

    @Test
    fun `should update reservation status to CONFIRMED when reservation exists`() {
        val reservation = StockReservation(
            id = "res-1",
            orderId = "order-1",
            productId = 1,
            quantity = 2,
            status = ReservationStatus.RESERVED,
            createdAt = LocalDateTime.now()
        )

        whenever(reservationRepository.findByOrderId("order-1")).thenReturn(reservation)
        whenever(reservationRepository.save(any())).thenAnswer { it.arguments[0] as StockReservation }

        service.execute(command)

        verify(reservationRepository).save(argThat {
            orderId == "order-1" && status == ReservationStatus.CONFIRMED
        })
    }

    @Test
    fun `should not save when reservation is not found`() {
        whenever(reservationRepository.findByOrderId("order-1")).thenReturn(null)

        service.execute(command)

        verify(reservationRepository, never()).save(any())
    }
}
