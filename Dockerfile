

# Stage 1: Build the application using official Eclipse Temurin JDK 25
FROM eclipse-temurin:25-jdk-noble AS build
# Stage 1: Build the application using JDK 25
#FROM maven:eclipse-temurin-25 AS build
WORKDIR /app

# Install Maven manually to ensure compatibility with JDK 25
RUN apt-get update && apt-get install -y maven

COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run the application using JRE 25
FROM eclipse-temurin:25-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]