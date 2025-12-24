# E-Commerce Backend API

A comprehensive Spring Boot-based e-commerce backend application with MongoDB, JWT authentication, payment integration, email notifications, and cloud storage.

## Tech Stack

- **Java 21** (Compiler: Java 23)
- **Spring Boot 3.5.8**
- **MongoDB** - NoSQL Database
- **Redis Cloud** - Distributed caching
- **Spring Security** - Authentication & Authorization
- **JWT** - Token-based authentication
- **Razorpay** - Payment Gateway Integration
- **Spring Mail** - Email notifications
- **Cloudflare R2** - Cloud storage (S3-compatible)
- **Lombok** - Reduce boilerplate code

## Features

### Core Features
- ✅ User authentication and authorization with JWT
- ✅ Product management with image uploads
- ✅ Shopping cart and wishlist functionality
- ✅ Order management and payment processing via Razorpay
- ✅ Email notifications for orders and reminders
- ✅ Automated cart and wishlist reminder emails
- ✅ Cloud-based image storage with Cloudflare R2
- ✅ RESTful API with comprehensive documentation
- ✅ MongoDB for flexible data storage

### Performance & Optimization
- ✅ Redis Cloud distributed caching for improved performance
- ✅ Async email processing
- ✅ Connection pooling for MongoDB
- ✅ AOP-based logging with performance monitoring
- ✅ GZIP compression for API responses
- ✅ Automatic cache invalidation on data changes

### Security
- ✅ JWT-based authentication
- ✅ Password strength validation
- ✅ BCrypt password hashing
- ✅ Role-based access control (RBAC)
- ✅ CORS configuration
- ✅ Custom exception handling

### Developer Experience
- ✅ Docker support with multi-stage builds
- ✅ Comprehensive error handling
- ✅ Input validation with Jakarta Validation
- ✅ Structured logging with Logback

## Prerequisites

Before you begin, ensure you have the following installed:

- **Java 21 or higher** - [Download JDK](https://www.oracle.com/java/technologies/downloads/)
- **Maven 3.6+** - [Download Maven](https://maven.apache.org/download.cgi)
- **MongoDB 4.4+** - [Download MongoDB](https://www.mongodb.com/try/download/community)
- **Git** - [Download Git](https://git-scm.com/downloads)

## Project Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd project
```

### 2. Configure MongoDB

Make sure MongoDB is running on your local machine:

```bash
# Start MongoDB service
# Windows (if installed as service):
net start MongoDB

# Or run MongoDB manually:
mongod --dbpath <path-to-data-directory>
```

The application will connect to MongoDB at: `mongodb://localhost:27017/ecommerce_db`

### 3. Configure Environment Variables

Create a `.env` file in the project root directory with the following configuration:

```properties
# Razorpay Payment Gateway
RAZORPAY_KEY_ID=your_razorpay_key_id
RAZORPAY_KEY_SECRET=your_razorpay_key_secret

# Email Configuration (Gmail SMTP)
EMAIL_USER=your_email@gmail.com
EMAIL_PASS=your_app_specific_password
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USER=your_email@gmail.com
SMTP_PASS=your_app_specific_password
FROM_EMAIL=your_email@gmail.com
ADMIN_EMAIL=admin_email@gmail.com

# Cloudflare R2 Storage
R2_ACCOUNT_ID=your_r2_account_id
R2_ACCESS_KEY_ID=your_r2_access_key
R2_SECRET_ACCESS_KEY=your_r2_secret_key
R2_BUCKET_NAME=ecommerce-images
R2_PUBLIC_URL=https://your-bucket.r2.dev

# Redis Cloud Configuration
REDIS_HOST=your-redis-cloud-host.cloud.redislabs.com
REDIS_PORT=12345
REDIS_PASSWORD=your_redis_password
REDIS_SSL_ENABLED=true

# Groq API (Optional - for AI features)
GROQ_API_KEY=your_groq_api_key
```

**Important Notes:**
- For Gmail, you need to generate an [App Password](https://support.google.com/accounts/answer/185833)
- For Razorpay, sign up at [Razorpay Dashboard](https://dashboard.razorpay.com/)
- For Cloudflare R2, create a bucket at [Cloudflare Dashboard](https://dash.cloudflare.com/)
- For Redis Cloud, sign up at [Redis Cloud](https://app.redislabs.com/) - See [REDIS_SETUP.md](REDIS_SETUP.md) for detailed instructions

### 4. Install Dependencies

```bash
mvnw clean install
```

Or if you have Maven installed globally:

```bash
mvn clean install
```

### 5. Run the Application

```bash
mvnw spring-boot:run
```

Or:

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Documentation

The application provides RESTful API endpoints. You can test them using:
- Postman collections (included in the project)
- cURL commands
- Any HTTP client

## Project Structure

```
project/
├── src/
│   ├── main/
│   │   ├── java/com/ecommerce/project/
│   │   │   ├── config/          # Configuration classes
│   │   │   ├── controller/      # REST Controllers
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── model/           # MongoDB Entity Models
│   │   │   ├── repository/      # MongoDB Repositories
│   │   │   ├── service/         # Business Logic
│   │   │   ├── security/        # JWT & Security Config
│   │   │   └── exception/       # Custom Exceptions
│   │   └── resources/
│   │       └── application.properties
│   └── test/                    # Unit & Integration Tests
├── .env                         # Environment Variables (not in git)
├── pom.xml                      # Maven Dependencies
└── README.md                    # This file
```

## Key Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token

### Products
- `GET /api/products` - Get all products
- `GET /api/products/{id}` - Get product by ID
- `POST /api/products` - Create new product (Admin)
- `PUT /api/products/{id}` - Update product (Admin)
- `DELETE /api/products/{id}` - Delete product (Admin)

### Cart & Wishlist
- `GET /api/cart` - Get user's cart
- `POST /api/cart/add` - Add item to cart
- `GET /api/wishlist` - Get user's wishlist
- `POST /api/wishlist/add` - Add item to wishlist

### Orders & Payments
- `POST /api/orders/create` - Create new order
- `POST /api/payments/razorpay/create` - Create Razorpay payment
- `POST /api/payments/razorpay/verify` - Verify payment

For complete endpoint details, check the API_ENDPOINTS_GUIDE.md file.

## Configuration

### Application Properties

Key configurations in `src/main/resources/application.properties`:

- **Server Port:** `8080`
- **MongoDB URI:** `mongodb://localhost:27017/ecommerce_db`
- **JWT Expiration:** `86400000ms` (24 hours)
- **File Upload Limit:** `10MB` per file, `50MB` per request
- **Cart Reminder Delay:** `30 minutes` (configurable)
- **Wishlist Reminder Delay:** `60 minutes` (configurable)

### Security

The application uses JWT-based authentication:
1. Register or login to receive a JWT token
2. Include the token in the `Authorization` header: `Bearer <token>`
3. Protected endpoints require valid JWT tokens

## Testing

### Run Tests

```bash
mvnw test
```

### Postman Collections

The project includes Postman collections for testing:
- `Ecommerce_API_Postman_Collection.json` - Main API endpoints
- `Razorpay_Postman_Collection.json` - Payment endpoints
- `R2_Image_Upload_Postman_Collection.json` - Image upload endpoints

Import these into Postman for quick testing.

## Additional Documentation

The project includes detailed guides for specific features:

- `API_ENDPOINTS_GUIDE.md` - Complete API reference
- `RAZORPAY_INTEGRATION_GUIDE.md` - Payment setup guide
- `EMAIL_INTEGRATION_GUIDE.md` - Email configuration
- `CLOUDFLARE_R2_SETUP_GUIDE.md` - Cloud storage setup
- `FRONTEND_DEVELOPMENT_PROMPT.md` - Frontend integration guide

## Troubleshooting

### MongoDB Connection Issues
- Ensure MongoDB is running: `mongod --version`
- Check connection string in `application.properties`
- Verify port 27017 is not blocked

### Email Not Sending
- Verify Gmail App Password is correct
- Check SMTP settings in `.env`
- Enable "Less secure app access" or use App Password

### Razorpay Payment Failures
- Verify API keys in `.env`
- Check Razorpay dashboard for test/live mode
- Review `RAZORPAY_TROUBLESHOOTING.md`

### Port Already in Use
```bash
# Change port in application.properties
server.port=8081
```

## Development

### Build for Production

```bash
mvnw clean package
```

This creates a JAR file in `target/` directory.

### Run Production Build

```bash
java -jar target/project-0.0.1-SNAPSHOT.jar
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the terms specified in the `pom.xml` file.

## Support

For issues and questions:
- Check the documentation files in the project root
- Review API_ENDPOINTS_GUIDE.md for endpoint details
- Check application logs for detailed error messages

## Quick Start Checklist

- [ ] Java 21+ installed
- [ ] MongoDB installed and running
- [ ] `.env` file configured with all credentials
- [ ] Dependencies installed (`mvnw clean install`)
- [ ] Application running (`mvnw spring-boot:run`)
- [ ] Test API endpoints via Postman or cURL

---

**Happy Coding! 🚀**
