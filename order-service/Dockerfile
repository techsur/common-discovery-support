FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY common-discovery-api common-discovery-api
COPY service-discovery-support service-discovery-support
COPY order-service order-service

RUN mvn -q -f common-discovery-api/pom.xml clean install -DskipTests
RUN mvn -q -f service-discovery-support/pom.xml clean install -DskipTests
RUN mvn -q -f order-service/pom.xml clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /workspace/order-service/target/order-service-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
