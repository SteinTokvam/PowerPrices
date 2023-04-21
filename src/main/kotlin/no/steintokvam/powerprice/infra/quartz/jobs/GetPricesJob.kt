package no.steintokvam.powerprice.infra.quartz.jobs

import no.steintokvam.powerprice.electricity.PriceService
import no.steintokvam.powerprice.infra.store.Store
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDate

class GetPricesJob: Job {
    private val LOGGER: Logger = LoggerFactory.getLogger(this::class.java)
    private val priceService = PriceService()

    override fun execute(context: JobExecutionContext?) {
        val today = LocalDate.now()

        if(Store.prices.isEmpty()) {
            Store.prices = priceService.getPrices(Store.zone, today)
        } else if(Store.prices.size >= 24) {
            Store.prices = emptyList()
            Store.prices = priceService.getPrices(Store.zone, today)
        }
        LOGGER.info("Got ${Store.prices.size} prices.")
        if(Store.prices.isEmpty()) {
            LOGGER.warn("Couldn't get any prices. used date: $today")
        }
        LOGGER.info("First price date: ${Store.prices[0].time_start} and lastPrice.start: ${Store.prices[Store.prices.size-1].time_start}")
    }
}