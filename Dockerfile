# Build Stage
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime Stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root system user for container security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy executable jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Switch to non-root user for security
USER appuser

ENV PORT=8080
EXPOSE ${PORT}

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -Xmx384m -jar app.jar"]
