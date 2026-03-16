# RevPay Microservices Monorepo
Spring Cloud microservices (Java 17) + Angular 16 SPA for RevPay. This README links the two halves so you can build, run, and debug quickly.

## What Is RevPay?
RevPay is a demo-grade digital payments platform: users authenticate, manage cards, hold wallet balances, request/send money, receive notifications, and run business-facing analytics. It’s built to showcase modern cloud patterns (API Gateway, service discovery, centralized config, JWT security) and a matching Angular client.

## Repo Layout
- `revpay_p3/` – Spring Boot 3.2 / Spring Cloud 2023 microservices: Config Server, Discovery (Eureka), API Gateway, Auth, Card, Notification, Wallet, Business. Maven build, Dockerfiles, Compose.
- `frontend/` – Angular 16 client using JWT auth against the API Gateway.

## Architecture Overview
Backend follows a typical Spring Cloud setup: Config Server feeds shared properties; services register with Eureka; API Gateway handles routing, CORS, and JWT verification, then forwards enriched headers (`X-User-Id`, `X-User-Email`, `X-User-Role`) to downstream services. Data is persisted to MySQL. Docker Compose wires the whole stack locally; services expose actuator endpoints for health and gateway route inspection. The Angular SPA talks only to the gateway.

## Services & Modules
### Infrastructure (backend)
- **Config Server (8888)** – centralized config (Git/native).
- **Discovery Server (8761)** – Eureka registry + dashboard.
- **API Gateway (8080)** – routing, JWT auth, CORS, header propagation.

### Business Services (backend)
- **Auth Service (8081)** – login/registration, JWT issuing, security flows.
- **Card Service (8082)** – card linking and verification.
- **Notification Service (8083)** – user notifications.
- **Wallet Service (8084)** – balances, transactions, money requests.
- **Business Service (8085)** – business accounts and analytics.

### Frontend (Angular)
- Single Angular app with routed screens: login/register, dashboard, wallet, transactions, requests, payment methods, business, analytics, notifications, security/recovery. Auth is enforced by `AuthGuard`; `AuthInterceptor` attaches the JWT.

## Prerequisites
- Java 17+, Maven 3.8+
- Node.js 18+ and npm (Angular CLI 16 optional)
- Docker & Docker Compose (recommended path)
- MySQL 8 only if you run services outside Docker

## Quick Start (recommended)
```bash
# 1) Backend stack
cd revpay_p3
docker-compose up -d

# 2) Frontend dev server
cd ../frontend
npm install
npm start     # http://localhost:4200
```
Gateway lives at `http://localhost:8080`; services expose 8081-8085; Discovery 8761; Config 8888; MySQL 3306.

## Backend Cheat Sheet (`revpay_p3/`)
- Build all: `mvn clean install`
- Run one service locally: `cd <service> && mvn spring-boot:run`
- Key env vars (compose sets defaults):
  - `JWT_SECRET`
  - `CONFIG_REPO_URI=https://github.com/revpay/config-repo`
  - `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`
  - `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE`
- API Gateway injects `X-User-Id`, `X-User-Email`, `X-User-Role` after JWT validation.
- Full details: `revpay_p3/README.md` (routes table, monitoring, troubleshooting).

## Frontend Cheat Sheet (`frontend/`)
- Dev: `npm start` → `http://localhost:4200`
- Build: `npm run build` → `dist/`
- Tests: `npm test`
- API base URL: `src/environments/environment.ts` (`http://localhost:8080/api/v1` by default)
- Auth is guarded by `AuthGuard`; `AuthInterceptor` adds JWT from `localStorage`.
- Full app notes: `frontend/README.md`.

## Ports
- Frontend 4200
- Gateway 8080
- Auth/Card/Notification/Wallet/Business 8081/8082/8083/8084/8085
- Discovery 8761
- Config Server 8888
- MySQL 3306

## Useful URLs
- Gateway health: `http://localhost:8080/actuator/health`
- Gateway routes: `http://localhost:8080/actuator/gateway/routes`
- Eureka dashboard: `http://localhost:8761`

## Troubleshooting
- Missing services in Eureka: ensure discovery-server is up and `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` matches.
- 401/403: confirm tokens, shared `JWT_SECRET`, and gateway is reachable from the client.
- CORS: add origins in API Gateway `CorsConfig`.
- DB issues: verify MySQL is running and credentials match datasource settings.
