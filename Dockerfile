# Use an official OpenJDK image (change version if needed)
FROM eclipse-temurin:21-jdk

# Set working directory
WORKDIR /app

# Copy JAR file from target folder
COPY target/SmartEmailReply-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Command to run the app
CMD ["java", "-jar", "app.jar"]
