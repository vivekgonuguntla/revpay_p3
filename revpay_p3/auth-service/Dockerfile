FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/auth-service-1.0.0.jar auth-service.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "auth-service.jar"]
