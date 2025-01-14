
# Stage 1: Build the Spring Boot application
FROM openjdk:17-slim AS builder

WORKDIR /app

# Copy the Maven project files
COPY pom.xml .
COPY src ./src

# Download dependencies and build the project
RUN apt-get update && apt-get install -y maven && \
    mvn clean package -DskipTests

# Stage 2: Create the runtime environment
FROM openjdk:17-slim

WORKDIR /app

# Copy the build artifact from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]