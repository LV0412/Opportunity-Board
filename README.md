# Opportunity Board

Opportunity Board is a web platform for university students to discover, save, apply for, and track student opportunities.

## Project Structure

```text
Opportunity Board/
  docs/
  backend/
  frontend/
```

## Prerequisites

- Node.js 20+
- npm 10+
- Java 21+
- Maven 3.9+
- PostgreSQL 15+

## Frontend

```bash
cd frontend
npm install
npm run dev
```

Default URL:

```text
http://localhost:5173
```

## Backend

Create a local PostgreSQL database named `opportunity_board`, or override the database settings with environment variables from `backend/.env.example`.

```bash
cd backend
mvn spring-boot:run
```

Default URL:

```text
http://localhost:8080
```

Health check:

```text
GET http://localhost:8080/api/health
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

## Environment

Each app owns its own environment template:

- Backend: copy `backend/.env.example` to `backend/.env` when local backend variables are needed.
- Frontend: copy `frontend/.env.example` to `frontend/.env` when local frontend variables are needed.

Vite reads `frontend/.env` automatically. For Spring Boot, set backend variables in your terminal, IDE run configuration, or deployment platform. Do not commit real `.env` files.
