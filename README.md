# Crypto Middleware Service

A robust, production-ready backend service built with **Spring Boot** that provides a unified API for cryptocurrency data. This middleware service acts as a proxy between clients and external cryptocurrency APIs, offering enhanced reliability, caching, and additional features.

## ✨ Features

- **Multi-Exchange Support**: Fetches cryptocurrency prices from multiple sources
- **Intelligent Caching**: Implements Spring Cache with Caffeine for high-performance data access
- **Resilient Design**: Built with resilience patterns to handle external API failures
- **Reactive Programming**: Uses Spring WebFlux for non-blocking, reactive data processing
- **Comprehensive API**: Well-documented REST endpoints with OpenAPI/Swagger UI
- **Monitoring**: Integrated with Spring Boot Actuator for health checks and metrics
- **Security**: API key authentication for secure access
- **Container Ready**: Docker support for easy deployment

## 🚀 Tech Stack

- **Java 21**
- **Spring Boot 3.2+**
- **Spring WebFlux** (Reactive)
- **Spring Cache** with **Caffeine**
- **Spring Doc OpenAPI**
- **Spring Boot Actuator**
- **Gradle 8.0+**
- **Docker**

## API Documentation

The API is versioned using URL path versioning. The current version is v1.

- Base URL: `/api/v1`
- Swagger UI: `/swagger-ui.html`
- OpenAPI JSON: `/v3/api-docs`

### Available Endpoints

#### Get Current Prices
```http
GET /api/v1/crypto/prices
```

#### Get Price History
```http
GET /api/v1/crypto/prices/history/{cryptoId}
```

#### Get Market Data
```http
GET /api/v1/crypto/market/{cryptoId}
```

## 🚀 Getting Started

### Prerequisites

- Java 21 or later
- Gradle 8.0 or later (or use the included `gradlew` script)
- Docker (optional, for containerized deployment)

### Local Development

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/crypto-middleware.git
   cd crypto-middleware
   ```

2. **Build the project**
   ```bash
   ./gradlew build
   ```

3. **Run the application**
   ```bash
   ./gradlew bootRun
   ```

The application will be available at: [http://localhost:8080](http://localhost:8080)

## 🐳 Docker Support

Build and run the application using Docker:

```bash
# Build the Docker image
docker build -t crypto-middleware .

# Run the container
docker run -p 8080:8080 crypto-middleware
```

## ☁️ Deployment

This application is container-ready and can be deployed to any cloud platform that supports Docker or Java applications, including:

- AWS ECS/EKS
- Google Cloud Run
- Azure Container Apps
- Heroku
- Render
- Railway
- Fly.io

## 🔒 Security

API key authentication is required for all endpoints. Include your API key in the `X-API-KEY` header with each request.

## 📊 Monitoring

Application metrics and health checks are available at:
```
http://localhost:8080/actuator/health
http://localhost:8080/actuator/metrics
```

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
