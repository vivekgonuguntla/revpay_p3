FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/wallet-service-0.0.1-SNAPSHOT.jar wallet-service.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "wallet-service.jar"]
