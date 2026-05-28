package br.com.souza.inventory_service

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@Disabled("Requires PostgreSQL and Kafka running")
class InventoryServiceApplicationTests {

	@Test
	fun contextLoads() {
	}

}
