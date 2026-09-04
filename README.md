# PricePulse

A Spring Boot price tracking and alert platform for books. Scrapes product prices from online retailers, maintains historical price data, analyzes price trends to generate buy recommendations, and notifies users when products reach their target price.

## Features

* **JWT Authentication** — Spring Security with stateless JWT authentication for registration, login, and protected endpoints.
* **Book Catalogue Import** — Automatically imports book details from Books to Scrape using Jsoup.
* **Automated Price Scraping** — Scheduled jobs periodically fetch and store the latest product prices using a pluggable `PriceScraper` architecture.
* **Price History Tracking** — Maintains historical prices for every product, allowing users to view the latest, cheapest, and historical average prices.
* **Buy Recommendation** — Analyzes the current price against historical prices and classifies products as `BUY NOW`, `GOOD TIME TO BUY`, `AVERAGE PRICE`, or `WAIT`.
* **Price Alerts** — Users can set target prices and receive email notifications when a product reaches the desired price.
* **Redis Caching** — `@Cacheable` / `@CacheEvict` caching for frequently requested price data such as latest and cheapest prices.
* **Admin Endpoints** — Admin-only endpoints for catalogue importing and system-level operations.
* **API Documentation** — Swagger/OpenAPI documentation with JWT bearer authentication support.

## Tech Stack

* **Java 21**, **Spring Boot**
* **Spring Security + JWT** (`jjwt`) — stateless authentication
* **Spring Data JPA** + **MySQL** — persistence
* **Redis** — price-data caching
* **Jsoup** — web scraping
* **Spring Scheduler** — automated price updates and alert checks
* **Spring Mail / Resend** — email notifications
* **springdoc-openapi** — Swagger UI
* **JUnit 5 + Mockito** — unit testing
* **Docker + GitHub Actions** — containerization and CI/CD

## Architecture

Layered architecture: `Controller → Service → Repository`, with:

* DTOs separating API requests/responses from JPA entities
* `GlobalExceptionHandler` for centralized error handling
* Dedicated scraper implementations behind the `PriceScraper` interface
* Scheduled price scraping and alert-processing jobs
* Redis caching for frequently accessed price information
* JWT-based authentication and role-based authorization

## API Overview

| Area     | Endpoints                                                              |
| -------- | ---------------------------------------------------------------------- |
| Auth     | `POST /auth/register`, `POST /auth/login`                              |
| Products | `GET /products`, `POST /products`, `GET /products/category/{category}` |
| Prices   | `GET /prices/{id}`, latest, cheapest, scrape, and suggestion endpoints |
| Alerts   | Create, list, and check price alerts                                   |
| Admin    | `POST /admin/import`                                                   |

Full request/response schemas are available through Swagger UI once the application is running.

## Running Locally

**Prerequisites:** Java 21, MySQL, Redis, and email credentials.

1. Create a MySQL database named `pricepulse` and configure the required environment variables/properties.
2. Ensure MySQL and Redis are running locally (`localhost:3306` and `localhost:6379`).
3. Run:

   ```bash
   ./mvnw spring-boot:run
   ```
4. The application starts on `http://localhost:8081`. Swagger UI is available at `http://localhost:8081/swagger-ui.html`.

## Configuration Notes

* Price scraping interval is configurable through `price.scrape.interval`.
* Database, Redis, JWT, and email credentials should be supplied through environment variables in production.
* Price scraping uses the configured retailer scrapers and stores each result as a historical price record.

## Tests

Run the test suite with:

```bash
./mvnw test
```

Tests cover core service logic, controllers, and price-scraping functionality.
