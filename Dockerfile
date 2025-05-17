# Stage 1: Build the application
FROM maven:3.9.6-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jdk-jammy
COPY --from=build /target/splitExpenses-0.0.1-SNAPSHOT.jar splitExpenses.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "splitExpenses.jar"]
