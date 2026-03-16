FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/business-service-0.0.1-SNAPSHOT.jar business-service.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "business-service.jar"]
