# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set environment variables
ENV PORT=8080 \
    JAVA_OPTS=""

# Set the working directory
WORKDIR /app

# Copy the application JAR file
COPY target/talkey-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE $PORT

# Command to run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
