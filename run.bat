@echo off

:: Create data directory if it doesn't exist
if not exist "data" mkdir data

:: Copy sample data if data files don't exist
if not exist "data\books.json" copy "sample_data\books.json" "data\" 2>nul
if not exist "data\users.json" copy "sample_data\users.json" "data\" 2>nul

:: Check if --no-build flag is present
if "%1"=="--no-build" (
    echo Skipping build...
    java -jar target\lib-management-1.0-SNAPSHOT-jar-with-dependencies.jar
    exit /b 0
)

:: Check if Maven is installed
where mvn >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo Maven is not installed. Please install Maven first.
    exit /b 1
)

:: Clean and build the project
echo Building the project...
call mvn clean package

:: Check if build was successful
if %ERRORLEVEL% equ 0 (
    echo Build successful. Running the application...
    java -jar target\lib-management-1.0-SNAPSHOT-jar-with-dependencies.jar
) else (
    echo Build failed. Please check the errors above.
    exit /b 1
) 