FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/card-service-1.0.0.jar card-service.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "card-service.jar"]
