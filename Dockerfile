# Multi-stage Dockerfile for TiaaDeals Spring Boot Backend

# Stage 1: Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage
FROM openjdk:17-jre-slim

# Set working directory
WORKDIR /app

# Create non-root user for security
RUN groupadd -r tiaadeals && useradd -r -g tiaadeals tiaadeals

# Copy the built JAR from build stage
COPY --from=build /app/target/tiaadeals-backend-1.0.0.jar app.jar

# Change ownership to non-root user
RUN chown tiaadeals:tiaadeals /app/app.jar

# Switch to non-root user
USER tiaadeals

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"] 