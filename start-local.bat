@echo off
echo Starting SwiftCart Backend for Local Development...
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 17 or higher
    pause
    exit /b 1
)

REM Check if Maven is available
call mvnw.cmd -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Maven wrapper not found
    pause
    exit /b 1
)

echo Java and Maven are available
echo.

REM Check if .env file exists
if not exist ".env" (
    echo ERROR: .env file not found
    echo Please copy .env.example to .env and configure it
    pause
    exit /b 1
)

echo Starting Spring Boot application...
echo Application will be available at: http://localhost:8080
echo API Documentation: http://localhost:8080/swagger-ui.html
echo.
echo Press Ctrl+C to stop the application
echo.

REM Start the application
call mvnw.cmd spring-boot:run

pause