FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY inventory-service inventory-service

RUN mvn -q -f inventory-service/pom.xml clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /workspace/inventory-service/target/inventory-service-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
