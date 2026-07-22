# ── Build ──────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
# Configuration Checkstyle (exécutée en phase validate lors du package).
COPY custom_sun_checks.xml checkstyle-suppressions.xml ./
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn package -DskipTests -q

# ── Run ───────────────────────────────────────────────
FROM eclipse-temurin:21-jre
WORKDIR /app
# curl est nécessaire au healthcheck (absent de l'image JRE de base).
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*
COPY --from=build /app/target/coroute-backend-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]