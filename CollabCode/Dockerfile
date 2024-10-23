# Use an official OpenJDK runtime as a parent image
FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the Spring application JAR file (make sure the path is correct)
COPY target/CollabCode-0.0.1-SNAPSHOT.jar /app/collabcode-backend.jar

# Copy the keystore for SSL (make sure the path is correct)
#COPY keystore.p12 /app/keystore.p12

# Expose the port on which the app runs
EXPOSE 8082

# Run the application with the keystore configuration
ENTRYPOINT ["java", "-jar", "/app/collabcode-backend.jar"]
