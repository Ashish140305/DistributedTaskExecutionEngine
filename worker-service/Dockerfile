# ============================================================
# Worker Service Dockerfile
# Multi-stage build: Maven build → JRE 21 slim runtime
# ============================================================

# Stage 1: Build (use non-Alpine for glibc compatibility with protoc)
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy parent POM and all module POMs first for dependency caching
COPY pom.xml .
COPY shared-proto/pom.xml shared-proto/
COPY common-library/pom.xml common-library/
COPY coordinator-service/pom.xml coordinator-service/
COPY worker-service/pom.xml worker-service/

# Download dependencies (cached unless POMs change)
RUN mvn dependency:go-offline -pl worker-service -am -B 2>/dev/null || true

# Copy all source code needed by worker and its dependencies
COPY shared-proto/src shared-proto/src
COPY common-library/src common-library/src
COPY worker-service/src worker-service/src

# Build the worker-service and its dependencies
RUN mvn clean package -pl worker-service -am -DskipTests -B

# Stage 2: Runtime (Alpine is fine for JRE-only)
FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

COPY --from=build /app/worker-service/target/*-exec.jar app.jar

RUN chown -R appuser:appgroup /app

USER appuser

# Actuator/metrics port (varies per instance via env)
EXPOSE 8081
# gRPC port (varies per instance via env)
EXPOSE 9091

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget -qO- http://localhost:${SERVER_PORT:-8081}/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
