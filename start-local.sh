#!/bin/bash

echo "Starting SwiftCart Backend for Local Development..."
echo

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed or not in PATH"
    echo "Please install Java 17 or higher"
    exit 1
fi

# Check if Maven wrapper is available
if [ ! -f "./mvnw" ]; then
    echo "ERROR: Maven wrapper not found"
    exit 1
fi

echo "Java and Maven are available"
echo

# Check if .env file exists
if [ ! -f ".env" ]; then
    echo "ERROR: .env file not found"
    echo "Please copy .env.example to .env and configure it"
    exit 1
fi

echo "Starting Spring Boot application..."
echo "Application will be available at: http://localhost:8080"
echo "API Documentation: http://localhost:8080/swagger-ui.html"
echo
echo "Press Ctrl+C to stop the application"
echo

# Make mvnw executable
chmod +x ./mvnw

# Start the application
./mvnw spring-boot:run