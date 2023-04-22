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
COPY --from=build ./target/powerprice-0.0.1-SNAPSHOT-jar-with-dependencies.jar powerprices.jar
ENTRYPOINT ["java","-jar","/powerprices.jar"]
