# PricePulse 📚

A book price aggregator and alert system built with Spring Boot.
Tracks prices across multiple retailers and notifies users when prices drop.

## 🔗 Live Demo
**Swagger UI:** https://pricepulse-production-ed3b.up.railway.app/swagger-ui.html

## ✨ Features
- Aggregates book prices from multiple retailers (real scraper + simulated retailers)
- Tracks price history with hourly automated scraping
- Buy suggestion engine (BUY NOW / WAIT / GOOD TIME TO BUY)
- Email alerts when price drops below user's target price
- Paginated product and price history APIs

## 🛠️ Tech Stack
- Java 21, Spring Boot 4.0.2
- Spring Data JPA, Spring Scheduler, Spring Validation
- MySQL 8 (Railway), Redis (cache-aside pattern)
- Jsoup (web scraping), Resend (email alerts)
- Swagger / SpringDoc OpenAPI
- Docker, Railway (deployment)

## 🚀 API Highlights
| Endpoint | Description |
|----------|-------------|
| `POST /admin/import` | Import book catalogue and scrape initial prices |
| `GET /products` | Get all products paginated |
| `GET /prices/{id}/latest` | Get latest price for a product |
| `GET /prices/{id}/cheapest` | Get lowest ever price |
| `GET /prices/{id}/suggestion` | Get buy suggestion based on price history |
| `POST /alerts` | Register email alert for price drop |

## ⚙️ Running Locally
1. Clone the repo
2. Copy `application-example.properties` to `application.properties` and fill in your values
3. Run `mvn spring-boot:run`
