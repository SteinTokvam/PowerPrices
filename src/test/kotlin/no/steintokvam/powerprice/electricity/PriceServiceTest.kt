package no.steintokvam.powerprice.electricity

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate

class PriceServiceTest {

    @Test
    fun testGetElectricityPrices(){
        val priceService = PriceService()
        val prices = priceService.getPrices("NO1", LocalDate.of(2023, 3, 18))
        assertTrue(prices[0].NOK_per_kWh == 1.1f)
    }
}