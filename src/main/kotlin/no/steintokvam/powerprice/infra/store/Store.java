package no.steintokvam.powerprice.infra.store;


import no.steintokvam.powerprice.electricity.ElectricityPrice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

public class Store {

    public static List<ElectricityPrice> prices = Collections.emptyList();
    public static String zone = "NO1";
    public static LocalDateTime getPricesUntil = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(7,0));
}
