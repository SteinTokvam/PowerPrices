#
# Build stage
#
FROM maven:3.8.3-openjdk-17 AS build
COPY src /src
COPY pom.xml ./
RUN mvn -f ./pom.xml clean package

#
# Package stage
#
FROM openjdk:17-oracle
COPY --from=build ./target/*.jar powerprices.jar
ENV zone="NO1"
EXPOSE 8081
ENTRYPOINT ["java","-jar","/powerprices.jar"]
