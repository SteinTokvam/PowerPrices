package no.steintokvam.powerprice.infra.store

import no.steintokvam.powerprice.electricity.ElectricityPrice
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class Store {
    companion object {
        var prices = emptyList<ElectricityPrice>()
        var zone = "NO1"
        var getPricesUntil = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(7, 0))
    }
}