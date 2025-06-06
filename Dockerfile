# Use a lightweight OpenJDK image
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Copy Maven build output (JAR file)
COPY target/ProjectTracker-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080


ENTRYPOINT ["java", "-jar", "app.jar"]
