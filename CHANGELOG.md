# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- GitHub Actions CI workflow (`.github/workflows/ci.yml`) — runs `./mvnw verify` (compile + all unit tests) on every push/PR to `main`, uploads the built JAR as a workflow artifact
- Blog posts CRUD API (FR5): `GET /posts`, `GET /posts/{id}`, `POST /posts`, `PUT /posts/{id}`, `DELETE /posts/{id}`
- Post entity, PostRequest/PostVO/PostDetailVO DTOs with validation (title <= 200, excerpt <= 500, date yyyy-MM-dd)
- NotFoundException and 404 handler in GlobalExceptionHandler for missing resources
- PostMapper, PostService, PostServiceImpl following the existing three-layer architecture
- PostMapperTest (10 tests), PostServiceTest (9 tests), PostControllerTest (10 tests) — total 57 tests passing

## [0.0.2] - 2026-11-11

### Added
- Three-layer unit tests (Service/Controller/Mapper) — 28 tests passing
- AdminTokenFilterTest — 4 tests covering admin endpoint auth (pass-through, missing/wrong/correct token)
- JaCoCo 0.8.12 coverage plugin with gates: overall LINE ≥ 70%, service impl ≥ 80%

### Changed
- Refactored to Alibaba Java coding standards (naming, structure, comments)
- H2 switched to file-mode persistence; schema.sql made idempotent (CREATE TABLE IF NOT EXISTS) so data survives restarts

### Fixed
- Spring Boot 4.0 test package paths (org.springframework.boot.webmvc.test.autoconfigure)

## [0.0.1] - 2026-11-10

### Added
- Initial project setup (Spring Boot 4.0.9, Java 21, MyBatis, H2)
- Requirements documentation (functional and non-functional)