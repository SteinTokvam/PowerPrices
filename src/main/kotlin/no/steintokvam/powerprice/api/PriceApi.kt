package no.steintokvam.powerprice.api

import no.steintokvam.powerprice.electricity.ElectricityPrice
import no.steintokvam.powerprice.electricity.PriceService
import no.steintokvam.powerprice.infra.store.Store
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
class PriceApi {

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/prices/all")
    fun getPrices(): List<ElectricityPrice> {
        LOGGER.info("Got called.")
        if(Store.prices.isEmpty()) {
            LOGGER.info("Has no power price to give!")
            return emptyList()
        }
        LOGGER.info("returning ${Store.prices}")
        return Store.prices
    }

    @GetMapping("/prices/cheapest/{from}/{hours}")
    fun getCheapestPricesFrom(@PathVariable from: LocalDateTime, @PathVariable hours: Int): List<ElectricityPrice> {
        LOGGER.info("Finding $hours cheapest hours from $from.")
        val lowestPrices = PriceService().getLowestPrices(from, hours)
        if(lowestPrices.isEmpty()) {
            LOGGER.info("Found no prices to return for the hours given.")
        }
        return lowestPrices
    }
}