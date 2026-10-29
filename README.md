# Job Portal with Admin Panel

Spring Boot 3 + Java 17 · Spring Data JPA · JdbcTemplate · MySQL · Spring Security + JWT (ADMIN role)
· AES-256-GCM encryption · Swagger/OpenAPI · HTML/CSS/JavaScript + jQuery AJAX frontend · Docker.

## Run option 1: Docker (easiest, only Docker needed)

    docker compose up --build

Open http://localhost:8080

## Run option 2: locally (Java 17+, Maven)

1. By default the app starts with an embedded H2 database, so no external database is required.
2. To use MySQL instead, set `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` (for example `root/root` with a local MySQL 8 instance).
3. `mvn spring-boot:run`
4. Open http://localhost:8080

## URLs

| Page            | URL                                                            |
| --------------- | -------------------------------------------------------------- |
| Job seeker site | http://localhost:8080/                                         |
| Admin panel     | http://localhost:8080/admin.html (login: **admin / admin123**) |
| Swagger UI      | http://localhost:8080/swagger-ui.html                          |

## Where each technology is used

| Resume skill                               | In this project                                                                         |
| ------------------------------------------ | --------------------------------------------------------------------------------------- |
| Spring Boot 3, REST APIs                   | `controller/` (public, auth, admin endpoints)                                           |
| JPA / Hibernate                            | `entity/`, `repository/`, `JobService` (jobs CRUD), application insert/update           |
| JdbcTemplate                               | `DashboardService` (stats SQL), `ApplicationService.list` (JOIN + pagination)           |
| MySQL                                      | `application.properties`, `docker-compose.yml`                                          |
| JWT, RBAC                                  | `security/JwtService`, `JwtAuthFilter`, `SecurityConfig` (`/api/admin/**` = ROLE_ADMIN) |
| AES-256 payload security                   | `security/AesGcmEncryptor`: candidate phone numbers are encrypted in the DB             |
| Validation, exception handling, pagination | DTO annotations, `GlobalExceptionHandler`, `PageResponse`                               |
| HTML, JavaScript, AJAX                     | `static/*.html`, `static/js/*.js` (jQuery `$.ajax` calls to the REST APIs)              |
| Swagger/OpenAPI, Docker                    | springdoc, `Dockerfile`, `docker-compose.yml`                                           |

## API summary

Public: `GET /api/public/jobs?q=&page=&size=` · `GET /api/public/jobs/{id}` · `POST /api/public/jobs/{id}/apply`
Auth: `POST /api/auth/login`
Admin (Bearer token): `GET /api/admin/dashboard` · jobs `GET/POST /api/admin/jobs`, `GET/PUT/DELETE /api/admin/jobs/{id}`
· applications `GET /api/admin/applications`, `PUT /api/admin/applications/{id}/status`, `DELETE /api/admin/applications/{id}`

## Configuration (env vars)

`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET` (32+ chars), `APP_AES_KEY` (Base64 of 32 bytes: `openssl rand -base64 32`),
`ADMIN_USERNAME`, `ADMIN_PASSWORD`. Change the defaults before any real deployment.

## Notes

- Spring Boot 3 needs Java 17+ (it will not start on Java 11).
- jQuery and Bootstrap load from CDN, so the browser needs internet access.

$env:JAVA_HOME = "C:\Users\udayb\.jdks\jdk-17.0.18"
$env:PATH = "$env:JAVA_HOME\bin;C:\Users\udayb\.maven\maven-3.10.0\bin;$env:PATH"
mvn spring-boot:run -DskipTests

If you want to confirm what is using port 8080
Get-NetTCPConnection -LocalPort 8080 | Format-Table -AutoSize

If you need to kill it manually:
Stop-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess -Force
