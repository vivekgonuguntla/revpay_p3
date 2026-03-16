# RevPay Microservices (P3)

RevPay is a comprehensive financial application built using a microservices architecture. This project demonstrates modern enterprise patterns including API Gateway, Service Discovery, and distributed configuration management.



## Architecture Overview

This project uses Spring Boot 3.2.0 and Spring Cloud 2023.0.0 to implement a microservices-based architecture with the following components:

### Infrastructure Services

1. **Config Server** (Port 8888)
   - Centralized configuration management
   - Supports both Git and native file system configuration

2. **Discovery Server** (Port 8761)
   - Eureka-based service registry
   - Enables service-to-service discovery
   - Web dashboard available at http://localhost:8761

3. **API Gateway** (Port 8080)
   - Single entry point for all client requests
   - JWT-based authentication
   - Routes requests to appropriate microservices
   - CORS configuration for frontend integration

### Business Services

4. **Auth Service** (Port 8081)
   - User authentication and registration
   - JWT token generation and validation
   - Security management endpoints

5. **Card Service** (Port 8082)
   - Credit/debit card management
   - Card linking and verification

6. **Notification Service** (Port 8083)
   - User notification management
   - Event-driven notifications

7. **Wallet Service** (Port 8084)
   - Digital wallet operations
   - Transaction management
   - Money request handling

8. **Business Service** (Port 8085)
   - Business account management
   - Business-specific operations

## Technology Stack

- **Java**: 17
- **Spring Boot**: 3.2.0
- **Spring Cloud**: 2023.0.0
- **Database**: MySQL 8.0
- **JWT**: JSON Web Tokens (jjwt 0.11.5)
- **Build Tool**: Maven
- **Containerization**: Docker & Docker Compose

## API Gateway Routes

The API Gateway routes requests as follows:

| Route | Target Service | Authentication Required |
|-------|---------------|------------------------|
| `/api/auth/**` | auth-service | No |
| `/api/v1/security/**` | auth-service | Yes |
| `/api/v1/cards/**` | card-service | Yes |
| `/api/v1/notifications/**` | notification-service | Yes |
| `/api/v1/wallet/**` | wallet-service | Yes |
| `/api/v1/transactions/**` | wallet-service | Yes |
| `/api/v1/requests/**` | wallet-service | Yes |
| `/api/v1/business/**` | business-service | Yes |

## JWT Authentication

The API Gateway validates JWT tokens and extracts user information:
- **X-User-Id**: User's unique identifier
- **X-User-Email**: User's email address
- **X-User-Role**: User's role (USER, ADMIN, BUSINESS)

These headers are automatically added to all downstream service requests.

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose (optional, for containerized deployment)
- MySQL 8.0 (if running services locally)

### Building the Project

```bash
# Navigate to project root
cd /Users/vimalkrishnan/Workspace/revature/2353/review/p2/repos/p3-revpay

# Build all modules
mvn clean install
```

### Running with Docker Compose

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

### Running Services Locally

1. **Start Config Server**
```bash
cd config-server
mvn spring-boot:run
```

2. **Start Discovery Server**
```bash
cd discovery-server
mvn spring-boot:run
```

3. **Start API Gateway**
```bash
cd api-gateway
mvn spring-boot:run
```

4. **Start Business Services** (auth, card, notification, wallet, business)
```bash
cd auth-service
mvn spring-boot:run
```

Repeat for other services.

## Environment Variables

Key environment variables for configuration:

```bash
# JWT Secret Key (should be at least 256 bits)
JWT_SECRET=revpay-secret-key-for-jwt-token-generation-and-validation-minimum-256-bits

# Config Repository (for Config Server)
CONFIG_REPO_URI=https://github.com/revpay/config-repo

# Database Configuration
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/revpay
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=rootpassword

# Eureka Server
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localhost:8761/eureka/
```

## Service URLs

When all services are running:

- **Config Server**: http://localhost:8888
- **Discovery Server**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **Auth Service**: http://localhost:8081
- **Card Service**: http://localhost:8082
- **Notification Service**: http://localhost:8083
- **Wallet Service**: http://localhost:8084
- **Business Service**: http://localhost:8085

## Development Guidelines

### Adding a New Microservice

1. Create a new module directory under the root
2. Add module to parent `pom.xml`
3. Include Eureka client dependency
4. Configure `application.yml` with service name and port
5. Register routes in API Gateway `RouteConfig.java`
6. Add service to `docker-compose.yml`

### CORS Configuration

The API Gateway is configured to allow requests from:
- http://localhost:3000 (React)
- http://localhost:4200 (Angular)
- http://localhost:5173 (Vite/Vue)

Update `CorsConfig.java` to add additional origins.

## Project Structure

```
p3-revpay/
├── pom.xml                    # Parent POM
├── .gitignore                 # Git ignore rules
├── docker-compose.yml         # Docker orchestration
├── README.md                  # This file
├── config-server/             # Configuration server
│   ├── pom.xml
│   └── src/
├── discovery-server/          # Eureka server
│   ├── pom.xml
│   └── src/
├── api-gateway/               # API Gateway
│   ├── pom.xml
│   └── src/
│       └── main/java/com/revpay/gateway/
│           ├── ApiGatewayApplication.java
│           ├── config/
│           │   ├── CorsConfig.java
│           │   └── RouteConfig.java
│           └── filter/
│               └── JwtAuthenticationFilter.java
├── auth-service/              # Authentication service
├── card-service/              # Card management service
├── notification-service/      # Notification service
├── wallet-service/            # Wallet and transaction service
└── business-service/          # Business account service
```

## Monitoring and Health Checks

All services expose actuator endpoints:

```bash
# Check service health
curl http://localhost:8080/actuator/health

# View API Gateway routes
curl http://localhost:8080/actuator/gateway/routes
```

## Troubleshooting

### Service not registering with Eureka
- Ensure Discovery Server is running
- Check `eureka.client.service-url.defaultZone` configuration
- Verify network connectivity between services

### JWT Authentication Failures
- Verify JWT_SECRET is consistent across services
- Check token expiration
- Ensure Authorization header format: `Bearer <token>`

### Database Connection Issues
- Verify MySQL is running
- Check database credentials
- Ensure database schema exists

## Contributing

This project is part of Revature P3. Please follow these guidelines:
1. Create feature branches from `main`
2. Follow Java coding conventions
3. Write unit tests for new features
4. Update documentation as needed

## License

Copyright 2024 Revature. All rights reserved.
