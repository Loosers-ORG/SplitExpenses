# Stage 1: Build the application
FROM maven:3.8.5-openjdk-17-slim AS build
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM openjdk:17-jdk-slim
COPY --from=build /target/splitExpenses-0.0.1-SNAPSHOT.jar splitExpenses.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "splitExpenses.jar"]
