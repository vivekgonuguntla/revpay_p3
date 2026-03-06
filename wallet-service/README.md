# Wallet Service - RevPay P3

## Owner
Sri Charani

## Description
The Wallet Service is a microservice for managing user wallets, transactions, and money requests in the RevPay application. It provides functionality for adding funds, withdrawing money, sending money between users, and managing money requests.

## Port
8084

## Technology Stack
- Java 17
- Spring Boot 3.1.5
- Spring Data JPA
- Spring Security
- MySQL
- OpenFeign (for inter-service communication)
- Lombok
- JWT

## Features
- Wallet Management (create, get balance, add funds, withdraw)
- Transaction Processing (send money, transaction history)
- Money Requests (create, view, respond to requests)
- Integration with Auth Service for user verification and PIN validation
- Integration with Card Service for card validation during fund additions
- Integration with Notification Service for transaction alerts

## Database Schema

### Wallets Table
- id (Long, Primary Key)
- user_id (Long, Unique, Not Null)
- balance (BigDecimal, Not Null)
- currency (String, Not Null)
- created_at (LocalDateTime, Not Null)

### Transactions Table
- id (Long, Primary Key)
- sender_wallet_id (Long)
- receiver_wallet_id (Long)
- amount (BigDecimal, Not Null)
- type (TransactionType, Not Null)
- status (TransactionStatus, Not Null)
- description (String)
- timestamp (LocalDateTime, Not Null)

### Money Requests Table
- id (Long, Primary Key)
- requester_id (Long, Not Null)
- payer_id (Long, Not Null)
- amount (BigDecimal, Not Null)
- note (String)
- status (RequestStatus, Not Null)
- created_at (LocalDateTime, Not Null)

## API Endpoints

### Wallet Endpoints
- `GET /api/v1/wallet/balance` - Get wallet balance
- `POST /api/v1/wallet/add-funds` - Add funds to wallet
- `POST /api/v1/wallet/withdraw` - Withdraw funds from wallet

### Transaction Endpoints
- `GET /api/v1/transactions` - Get transaction history
- `POST /api/v1/transactions/send` - Send money to another user

### Money Request Endpoints
- `POST /api/v1/requests/create` - Create a money request
- `GET /api/v1/requests` - Get all money requests
- `POST /api/v1/requests/{id}/respond` - Respond to a money request

## Configuration

### Application Properties
The service uses the following configuration (application.yml):
- Server port: 8084
- Database: MySQL (revpay_wallet)
- Auth Service URL: http://localhost:8081
- Card Service URL: http://localhost:8082
- Notification Service URL: http://localhost:8085

### Environment Variables
You can override the following properties using environment variables:
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SERVICE_AUTH_URL`
- `SERVICE_CARD_URL`
- `SERVICE_NOTIFICATION_URL`

## Building and Running

### Build
```bash
mvn clean package
```

### Run Locally
```bash
mvn spring-boot:run
```

### Run with Docker
```bash
# Build the application
mvn clean package

# Build Docker image
docker build -t wallet-service:latest .

# Run container
docker run -p 8084:8084 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/revpay_wallet \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=root \
  wallet-service:latest
```

## Dependencies
This service depends on:
- Auth Service (port 8081) - for user authentication and PIN verification
- Card Service (port 8082) - for card validation
- Notification Service (port 8085) - for sending notifications
- MySQL Database

## Security
The service uses JWT-based authentication. All endpoints require a valid JWT token in the Authorization header and user ID in the X-User-Id header.

## Error Handling
The service provides comprehensive error handling for:
- Insufficient funds
- Invalid PIN
- User not found
- Card validation failures
- General runtime exceptions
- Validation errors

## Transaction Types
- SEND - Money sent to another user
- RECEIVE - Money received from another user
- REQUEST - Money request transaction
- DEPOSIT - Funds added to wallet
- WITHDRAWAL - Funds withdrawn from wallet
- PAYMENT - Payment transaction

## Request Status
- PENDING - Request awaiting response
- ACCEPTED - Request accepted
- DECLINED - Request declined
- CANCELLED - Request cancelled

## Notes
- All monetary values use BigDecimal for precision
- Transactions are atomic and use Spring's @Transactional
- The service maintains referential integrity between wallets and transactions
- Notifications are sent asynchronously and failures don't affect transaction completion
