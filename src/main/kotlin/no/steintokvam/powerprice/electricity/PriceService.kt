package no.steintokvam.powerprice.electricity

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.module.kotlin.KotlinModule
import no.steintokvam.powerprice.infra.store.Store
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*


class PriceService {
    private val LOGGER: Logger = LoggerFactory.getLogger(this::class.java)
    private val BASE_URL = "https://www.hvakosterstrommen.no/api/v1/prices"
    private val client = OkHttpClient()
    private val formatter = DateTimeFormatter.ISO_DATE_TIME
    private val dateTimeDeserializer = LocalDateTimeDeserializer(formatter)
    private val dateTimeSerializer = LocalDateTimeSerializer(formatter)
    private val javaTimeModule = JavaTimeModule()
        .addSerializer(LocalDateTime::class.java, dateTimeSerializer)
        .addDeserializer(LocalDateTime::class.java, dateTimeDeserializer)
    private val mapper = ObjectMapper()
        .registerModule(KotlinModule.Builder().build())
        .registerModule(javaTimeModule)

    private val ALL_ZONES = listOf("NO1", "NO2", "NO3", "NO4", "NO5")

    fun getPrices(zone: String, date: LocalDate): List<ElectricityPrice> {
        val prices: MutableList<ElectricityPrice> = getPriceForDay(zone, date)

        if(LocalTime.now().isAfter(LocalTime.of(14,0))) {
            prices.addAll(getPriceForDay(zone, date.plusDays(1)))
        }

        return prices
    }

    fun getLowestPrices(
        date: LocalDateTime,
        hoursToGet: Int
    ) : List<ElectricityPrice> {
        val allPrices = Store.prices
        if(Store.getPricesUntil.isBefore(LocalDateTime.now())) {
            Store.getPricesUntil = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(7, 0))
        }

        if(getHoursBetween(date, Store.getPricesUntil) < hoursToGet || allPrices.isEmpty()) {
            //tar lengre tid å lade enn man har til den er ferdig aka må kjøre på eller man har ingen priser
            return emptyList()
        }

        val sortedPrices = allPrices
            .filter { it.time_start.isAfter(date) || it.time_start.hour == date.hour }
            .filter { it.time_start.isBefore(Store.getPricesUntil) }
            .sortedBy { it.NOK_per_kWh }

        if(sortedPrices.size < hoursToGet) {
            LOGGER.error("Has ${sortedPrices.size} prices, but expected to have at least $hoursToGet prices.")
            LOGGER.info("Has ${allPrices.size} unfiltered prices. Finnish charging by is: ${Store.getPricesUntil}")
            LOGGER.info("All prices:")
            allPrices.forEach { LOGGER.info("${it.time_start}") }
            return sortedPrices
        }
        return sortedPrices
            .subList(0, hoursToGet)
    }

    fun getHoursBetween(first: LocalDateTime, second: LocalDateTime): Int {//TODO: burde være i en util klasse
        return ChronoUnit.HOURS.between(first, second).toInt()
    }

    private fun getPriceForDay(zone: String, date: LocalDate): MutableList<ElectricityPrice> {
        if (!ALL_ZONES.contains(zone)) {
            LOGGER.warn(String.format("Zone %s is not valid", zone))
            return mutableListOf()
        }
        var response: Response? = null
        try {
            val request = createRequest(zone, date)
            response = client.newCall(request).execute()

            val collectionType =
                mapper.typeFactory.constructCollectionType(List::class.java, ElectricityPrice::class.java)
            return mapper.readValue<List<ElectricityPrice>>(response.body?.charStream()?.readText(), collectionType)
                .onEach { it.NOK_per_kWh = it.NOK_per_kWh.format(2) }.toMutableList()
        } catch (e: Exception) {
            LOGGER.warn("Couldn't get prices for $date in zone $zone.")
            LOGGER.error(e.toString())
        } finally {
            response?.close()
        }
    }

    private fun Float.format(scale: Int) = "%.${scale}f".format(Locale.US, this).toFloat()

    private fun createRequest(zone: String, date: LocalDate): Request {
        val day = date.dayOfMonth
        val month = date.monthValue
        val monthEndpoint = if (month > 10)
            "/$month"
        else
            "/0$month"

        val dayEndpoint = if (day > 10)
            "-$day"
        else
            "-0$day"

        val endpoint =
            "/" + date.year + monthEndpoint + dayEndpoint + "_" + zone + ".json"


        return Request.Builder()
            .url(BASE_URL + endpoint)
            .get()
            .addHeader("accept", "application/json")
            .build()
    }
}
