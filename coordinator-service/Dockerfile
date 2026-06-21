# ============================================================
# Coordinator Service Dockerfile
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
RUN mvn dependency:go-offline -pl coordinator-service -am -B 2>/dev/null || true

# Copy all source code needed by coordinator and its dependencies
COPY shared-proto/src shared-proto/src
COPY common-library/src common-library/src
COPY coordinator-service/src coordinator-service/src

# Build the coordinator-service and its dependencies
RUN mvn clean package -pl coordinator-service -am -DskipTests -B

# Stage 2: Runtime (Alpine is fine for JRE-only)
FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

COPY --from=build /app/coordinator-service/target/*.jar app.jar

RUN chown -R appuser:appgroup /app

USER appuser

# REST API port
EXPOSE 8080
# gRPC port
EXPOSE 9090

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
