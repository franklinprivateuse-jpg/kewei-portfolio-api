# Portfolio Backend API

## Overview

Backend REST API for a digital portfolio. Two main services:

- **Chat** — visitors send messages and get auto-replies
- **Blog** — owner manages blog posts

Full requirements: [`requirements/`](requirements/)

## Live Demo

TBD

## Features

- Chat with keyword-based auto-reply
- Chat history per session
- Blog CRUD (create, read, update, delete)
- Input validation
- Admin token auth (`X-Admin-Token`)
- CORS for frontend
- Swagger UI

## Technology Stack

| Category | Technology |
|----------|-----------|
| Language | Java 21 |
| Framework | Spring Boot 4.0.9 |
| Database | H2 (file mode) |
| ORM | MyBatis |
| API Docs | springdoc-openapi |
| Build | Maven |
| Auth | Spring Security |
| Utility | Lombok |

## System Architecture

```
Controller → Service → Mapper (MyBatis) → H2
```

- **Controller** — HTTP requests and responses
- **Service** — business logic
- **Mapper** — database access

Details: [requirements document](requirements/Functional%20requirements/portfolio-api-requirements.md)

## Project Structure

```
kewei-portfolio-api/
├── requirements/
│   ├── Functional requirements/
│   └── Non-functional requirements/
├── src/
│   ├── main/
│   └── test/
└── pom.xml
```

## Installation

Requires Java 21+.

```bash
git clone https://github.com/franklinprivateuse-jpg/kewei-portfolio-api.git
cd kewei-portfolio-api
```

## Running the Project

```bash
mvnw.cmd spring-boot:run
```

- API: `http://localhost:8080`
- H2 Console: `http://localhost:8080/h2-console`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

## Build

```bash
mvnw.cmd clean package
```

JAR output in `target/`.

## Testing

```bash
mvnw.cmd test
```

Standards: [Unit Testing Strategy](requirements/Non-functional%20requirements/unit-testing-strategy-and-standards.md)

## CI/CD Pipeline

TBD

## Deployment

TBD

## API Integration

Base URL: `/api/v1`

| Method | Path | Description | Admin |
|--------|------|-------------|-------|
| POST | /messages | Send message, get auto-reply | |
| GET | /messages?sessionId={id} | Get chat history | |
| GET | /messages/all | Get all messages | Yes |
| DELETE | /messages?sessionId={id} | Clear chat history | |
| GET | /posts | List posts | |
| GET | /posts/{id} | Get one post | |
| POST | /posts | Create post | Yes |
| PUT | /posts/{id} | Update post | Yes |
| DELETE | /posts/{id} | Delete post | Yes |

Admin endpoints need `X-Admin-Token` header. Full spec: [API Specification](requirements/Functional%20requirements/portfolio-api-requirements.md)

## Innovation Features

- Keyword-based auto-reply chat
- Session-based chat history (frontend-generated UUID)
- Simple single-password admin auth

## Screenshots

TBD

## Changelog

| Version | Date | Changes |
|---------|------|---------|
| 0.0.1 | 2026-11 | Initial setup, requirements docs |

## Future Improvements

- User accounts instead of single password
- Pagination
- Image upload
- PostgreSQL instead of H2
- Rate limiting

## Author

Kewei — University of Limerick, CS5709

## License

TBD