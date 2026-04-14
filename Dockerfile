FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/pricepulse-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=demo", "app.jar"]