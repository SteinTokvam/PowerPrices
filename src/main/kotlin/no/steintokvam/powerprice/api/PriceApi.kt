package no.steintokvam.powerprice.api

import no.steintokvam.powerprice.electricity.ElectricityPrice
import no.steintokvam.powerprice.infra.store.Store
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PriceApi {

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @GetMapping
    fun getPrices(): List<ElectricityPrice> {
        if(Store.prices.isEmpty()) {
            LOGGER.info("Has no power price to give!")
            return emptyList()
        }
        return Store.prices
    }
}