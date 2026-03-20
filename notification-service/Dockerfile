FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY notification-service notification-service

RUN mvn -q -f notification-service/pom.xml clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /workspace/notification-service/target/notification-service-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
