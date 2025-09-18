# Etapa de build
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package && mv target/*.jar app.jar

# Runtime
FROM eclipse-temurin:17-jre
WORKDIR /app
ENV APP_VERSION=dev
# Instalar curl para healthcheck
USER root
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*
COPY --from=builder /workspace/app.jar /app/app.jar
EXPOSE 8080
HEALTHCHECK --interval=10s --timeout=3s --retries=6 CMD curl -fsS http://localhost:8080/health || exit 1
ENTRYPOINT ["java","-jar","/app/app.jar"]