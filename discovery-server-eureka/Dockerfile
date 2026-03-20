FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY discovery-server-eureka discovery-server-eureka

RUN mvn -q -f discovery-server-eureka/pom.xml clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /workspace/discovery-server-eureka/target/discovery-server-eureka-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
