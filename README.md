# PowerPrices
![example workflow](https://github.com/steintokvam/powerprices/actions/workflows/main.yml/badge.svg?event=push)

Checks for electricity prices in Norway and serves them on a couple of endpoints. Currently used for [Smartcharging](https://github.com/SteinTokvam/SmartCharger) my easee charger.

## Usage
Available endpoints are 

```
/prices/all
```
Which will give all currently available prices which are the prices for today and if they are available yet also the prices for tomorrow.

```
/prices/cheapest/{from}/{hours}/{to}
```
Which gives a number of cheapest prices specified by hours in the range specified by from and to.
e.g:
```
/prices/cheapest/2023-05-10T10:00/2/2023-05-10T20:00
```
Will return the 2 cheapest hours for May 10, 2023 between 13(1PM) and 20(8PM). That list of prices will be sorted by cheapest to most expensive hour, and not by when the prices are valid.

Prices are returned as a list of json objects declared like this:
```json
{
  "time_start": LocalDateTime,
  "time_end": LocalDateTime,
  "eur_per_kWh": Float,
  "exr": Float,
  "nok_per_kWh": Float
}
```

## Docker
Can be found at [Dockerhub](https://hub.docker.com/r/steintokvam/powerprices).
The app expects an environment variable zone that by default is NO1, but can be set to the following values:

NO1 | NO2 | NO3 | NO4 | NO5
