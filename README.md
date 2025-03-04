# Library Management System

A Project Based Learning (PBL) implementation of a Library Management System.

## Student Information
- **Name:** Taraka Srinivas
- **Roll Number:** 23EG109A34
- **Course:** Object Oriented Programming
- **Institution:** Anurag University

## Features

- Book Management (Add, Edit, Delete)
- User Management (Add, Edit, Delete)
- Book Borrowing System
- JSON-based Data Persistence
- Modern UI with FlatLaf Look and Feel

## Prerequisites

- Java 11 or higher
- Maven

## Building and Running

1. Clone the repository
2. Make the run script executable:
   ```bash
   chmod +x run.sh
   ```
3. Run the application:
   ```bash
   ./run.sh
   ```

   ```bash
   .\run.bat
   ```
   
   To run without rebuilding:
   ```bash
   ./run.sh --no-build
   ```
   
   To run without rebuilding:
   ```bash
   .\run.bat --no-build
   ```

Alternatively, you can build and run manually:

1. Build the project:
   ```bash
   mvn clean package
   ```
2. Run the application:
   ```bash
   java -jar target/lib-management-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

## Sample Data

Sample data is automatically copied to the `data` directory when running the application for the first time. The `sample_data` directory contains:
- `books.json`: Sample book records
- `users.json`: Sample user records

## Data Storage

The application stores all data in JSON files in the `data` directory:
- `data/books.json`: Contains all book records
- `data/users.json`: Contains all user records

These files are automatically created when needed.

## Usage

1. **Managing Books**
   - Click on the "Books" tab
   - Use the buttons at the bottom to add, edit, or delete books
   - The table shows all books with their current status

2. **Managing Users**
   - Click on the "Users" tab
   - Use the buttons at the bottom to add, edit, or delete users
   - The table shows all users and their borrowed books count

## Contributing

This is a student project created for learning purposes. Feel free to fork the repository and submit pull requests for any improvements. 