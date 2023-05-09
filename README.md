# PowerPrices
![example workflow](https://github.com/steintokvam/powerprices/actions/workflows/main.yml/badge.svg?event=push)

Checks for electricity prices in Norway and serves them on a couple of endpoints. Currently used for [Smartcharging](https://github.com/SteinTokvam/SmartCharger) my easee charger.

## Docker
Can be found at [Dockerhub](https://hub.docker.com/r/steintokvam/powerprices).
The app expects an environment variable zone that by default is NO1, but can be set to the following values:

NO1 | NO2 | NO3 | NO4 | NO5
