# Local Development Setup Guide

## Prerequisites

1. **Java 17 or higher** - Check with `java -version`
2. **Maven 3.6+** - Check with `mvn -version`
3. **Internet connection** - For MongoDB Atlas cloud database

## Option 1: Run with Docker Compose (Recommended)

This will start the Spring Boot application connected to MongoDB Atlas:

```bash
# Start the application
docker-compose up -d

# View logs
docker-compose logs -f

# Stop the application
docker-compose down
```

## Option 2: Run Locally with Maven

### Step 1: Verify Configuration

Make sure your `.env` file has the correct MongoDB Atlas connection:
```properties
MONGODB_URI=mongodb+srv://athenaecom2024_db_user:3S3UFTk1tvHFDkOX@swiftcart-db.btm7ijg.mongodb.net/?appName=SwiftCart-DB
```

### Step 2: Run the Spring Boot Application

```bash
# Using Maven wrapper (recommended)
./mvnw spring-boot:run

# Or using Maven directly
mvn spring-boot:run

# Or build and run JAR
./mvnw clean package
java -jar target/project-0.0.1-SNAPSHOT.jar
```

## Testing the Setup

Once the application is running, you can test it:

1. **Health Check**: http://localhost:8080/actuator/health
2. **API Documentation**: http://localhost:8080/swagger-ui.html
3. **API Base URL**: http://localhost:8080/api

## Environment Variables

The application uses these key environment variables:

- `MONGODB_URI` - MongoDB Atlas connection string (already configured)
- `JWT_SECRET` - Secret key for JWT tokens
- `RAZORPAY_KEY_ID` & `RAZORPAY_KEY_SECRET` - Payment gateway credentials
- `EMAIL_USER` & `EMAIL_PASS` - SMTP credentials for sending emails
- `R2_*` - Cloudflare R2 storage credentials for file uploads

## Troubleshooting

### MongoDB Connection Issues
- Ensure you have internet connectivity for MongoDB Atlas
- Verify the connection string in `.env` is correct
- Check if your IP address is whitelisted in MongoDB Atlas

### Port Already in Use
```bash
# Find process using port 8080
netstat -ano | findstr :8080  # Windows
lsof -i :8080                 # macOS/Linux

# Kill the process
taskkill /PID <PID> /F        # Windows
kill -9 <PID>                 # macOS/Linux
```

### Application Logs
Check the logs in the `logs/` directory or console output for detailed error messages.

## Development Tips

1. **Hot Reload**: Use `./mvnw spring-boot:run` for automatic restart on code changes
2. **Database GUI**: Use MongoDB Compass to view/edit data in Atlas
3. **API Testing**: Use Postman or the included Swagger UI
4. **Profiles**: Use different Spring profiles for different environments