package no.steintokvam.powerprice

import no.steintokvam.powerprice.electricity.PriceService
import no.steintokvam.powerprice.infra.quartz.QuartzSchedueler
import no.steintokvam.powerprice.infra.store.Store
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.time.LocalDate

@SpringBootApplication
open class PowerPriceApplication

private val LOGGER = LoggerFactory.getLogger(PowerPriceApplication::class.java)

fun main(args: Array<String>) {

	runApplication<PowerPriceApplication>(*args)
	//Store.zone = System.getenv("zone")
	if(Store.zone == null) {
		LOGGER.error("Zone not set.")
		return
	}
	LOGGER.info("Got zone ${Store.zone}.")
	Store.prices = PriceService().getPrices(Store.zone, LocalDate.now())
	QuartzSchedueler().schedueleJobs()
}

