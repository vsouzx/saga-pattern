package br.com.souza.inventory_service.application.domain.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ReservationStatusTest {

    @Test
    fun `should have exactly three statuses`() {
        val statuses = ReservationStatus.entries
        assertEquals(3, statuses.size)
    }

    @Test
    fun `should contain RESERVED CONFIRMED and RELEASED`() {
        assertEquals(ReservationStatus.RESERVED, ReservationStatus.valueOf("RESERVED"))
        assertEquals(ReservationStatus.CONFIRMED, ReservationStatus.valueOf("CONFIRMED"))
        assertEquals(ReservationStatus.RELEASED, ReservationStatus.valueOf("RELEASED"))
    }
}
