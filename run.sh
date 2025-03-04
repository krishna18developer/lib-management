#!/bin/bash

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Maven is not installed. Please install Maven first."
    exit 1
fi

# Clean and build the project
echo "Building the project..."
mvn clean package

# Check if build was successful
if [ $? -eq 0 ]; then
    echo "Build successful. Running the application..."
    # Run the application
    java -jar target/lib-management-1.0-SNAPSHOT-jar-with-dependencies.jar
else
    echo "Build failed. Please check the errors above."
    exit 1
fi 