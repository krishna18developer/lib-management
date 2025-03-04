#!/bin/bash

# Create data directory if it doesn't exist
mkdir -p data

# Copy sample data if data files don't exist
if [ ! -f "data/books.json" ]; then
    cp sample_data/books.json data/ 2>/dev/null || :
fi
if [ ! -f "data/users.json" ]; then
    cp sample_data/users.json data/ 2>/dev/null || :
fi

# Check if --no-build flag is present
if [ "$1" = "--no-build" ]; then
    echo "Skipping build..."
    java -jar target/lib-management-1.0-SNAPSHOT-jar-with-dependencies.jar
    exit 0
fi

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