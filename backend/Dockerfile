# Multi-stage build for Spring Boot application
FROM eclipse-temurin:17-jdk as builder

# Set working directory
WORKDIR /app

# Copy gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .

# Make gradlew executable
RUN chmod +x gradlew

# Copy source code
COPY src src

# Build the application
RUN ./gradlew build -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:17-jre

# Accept build arguments
ARG JWT_SECRET
ARG JWT_EXPIRATION
ARG JWT_REFRESH_EXPIRATION
ARG OPENAI_API_KEY

# Create app user
RUN addgroup --system spring && adduser --system spring --ingroup spring

# Set working directory
WORKDIR /app

# Copy the built jar from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Change ownership to spring user
RUN chown spring:spring app.jar

# Set environment variables
ENV JWT_SECRET=${JWT_SECRET}
ENV JWT_EXPIRATION=${JWT_EXPIRATION}
ENV JWT_REFRESH_EXPIRATION=${JWT_REFRESH_EXPIRATION}
ENV OPENAI_API_KEY=${OPENAI_API_KEY}

# Switch to spring user
USER spring

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM optimization for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Run the application with Flyway configuration
CMD ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar --spring.profiles.active=dev --spring.datasource.url=jdbc:h2:mem:testdb --spring.jpa.hibernate.ddl-auto=none --spring.sql.init.mode=never --spring.jpa.defer-datasource-initialization=false --spring.flyway.enabled=true --spring.flyway.baseline-on-migrate=true --spring.flyway.baseline-version=0 --spring.flyway.locations=classpath:db/migration"]
