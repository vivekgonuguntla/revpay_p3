FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/config-server-1.0.0.jar config-server.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "config-server.jar"]
