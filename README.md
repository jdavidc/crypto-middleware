# Crypto Price Proxy Middleware

A simple backend service built with **Spring Boot** that acts as a middleware (proxy) for fetching cryptocurrency prices from the [CoinGecko API](https://www.coingecko.com/).  
It provides a stable API for clients by caching data and handling rate limits gracefully.

## ✨ Features

- Fetches live prices for **Bitcoin** and **Ethereum** in USD.
- Uses **Caffeine Cache** to store the latest prices.
- Scheduled background refresh every 2 minutes.
- Serves cached data to avoid CoinGecko's `429 Too Many Requests` errors.
- REST endpoint ready to be consumed by web or mobile apps.
- Lightweight and deployable to free hosting platforms.

## 🚀 Tech Stack

- **Java 21+**
- **Spring Boot 3**
- **Spring WebFlux** (WebClient)
- **Caffeine Cache**
- **Gradle**

## 📡 API Endpoints

### Get current crypto prices
```http
GET /api/prices
#### Response example:
{
  "bitcoin": {
    "usd": 64235.12
  },
  "ethereum": {
    "usd": 3275.45
  }
}
```
If the CoinGecko API is unavailable or rate-limited, the service will return the last cached values.

⚙️ Running Locally
Prerequisites

Java 21+ installed

Gradle (or use the included ./gradlew script)

Steps
# Clone the repository
git clone https://github.com/your-username/crypto-price-proxy.git
cd crypto-price-proxy

# Build the project
./gradlew build

# Run the app
./gradlew bootRun


The app will be available at:
👉 http://localhost:8080/api/prices

☁️ Deployment

This project can be deployed to free hosting services such as:

Render

Railway

Fly.io

Simply build the JAR and deploy it with your preferred service.

📖 Next Steps / Ideas

Add support for more cryptocurrencies.

Allow dynamic selection of fiat currencies (e.g., EUR, GBP).

Implement API key support for controlled access.

Add Dockerfile for containerized deployments.
