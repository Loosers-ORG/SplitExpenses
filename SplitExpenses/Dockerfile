# Stage 1: Build the application with Maven 3.9.6 & JDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run the application using JDK 21 runtime
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/splitExpenses-0.0.1-SNAPSHOT.jar splitExpenses.jar

# Expose port 8080 (or dynamic PORT for platforms like Render/Fly.io)
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "splitExpenses.jar"]
