# Stage 1: Build stage
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and POM for dependency layer caching
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Fix Windows CRLF line endings on mvnw and set executable permission
RUN tr -d '\r' < mvnw > mvnw.tmp && mv mvnw.tmp mvnw && chmod +x mvnw

# Pre-fetch dependencies to speed up future builds
RUN ./mvnw dependency:go-offline -B || true

# Copy source code and build final package
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Stage 2: Production Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create non-root system user for container security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy executable jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Switch to non-root user
USER appuser

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]