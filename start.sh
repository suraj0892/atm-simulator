#!/bin/bash

# Ensure the environment is properly set
if [[ -z "$JAVA_HOME" ]] || [[ ! -x "$JAVA_HOME/bin/java" ]]; then
    echo "Error: JAVA_HOME is not set or Java is not installed."
    exit 1
fi

# Print environment information
echo "Environment is correctly configured:"
echo "JAVA_HOME: $JAVA_HOME"

# Build the application using Gradle
echo "Building the application using Gradle..."
./gradlew build

if [ $? -ne 0 ]; then
    echo "Error: Gradle build failed."
    exit 1
fi

# Run the application using the 'runCli' Gradle task
echo "Starting the application..."
java -jar build/libs/atmsimulator-1.0-SNAPSHOT.jar

# Check if the application started successfully
if [ $? -ne 0 ]; then
    echo "Error: Failed to start the application."
    exit 1
fi
