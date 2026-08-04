FROM eclipse-temurin:26-jre

WORKDIR /app

COPY target/rate_limiter-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]