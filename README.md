# TradingServer

A Spring Boot REST API server for simulating cryptocurrency and stock trading operations. This server provides endpoints for buying and selling both cryptocurrencies and stocks with real-time price fetching from external APIs.

## Table of Contents

- [Overview](#overview)
- [Technology Stack](#technology-stack)
- [Features](#features)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [API Endpoints](#api-endpoints)
  - [Cryptocurrency Trading](#cryptocurrency-trading)
  - [Stock Trading](#stock-trading)
- [API Usage Examples](#api-usage-examples)
- [External APIs Used](#external-apis-used)
- [Development](#development)

## Overview

TradingServer is a backend application that simulates trading operations for both cryptocurrencies and stocks. It fetches real-time prices from external APIs and provides RESTful endpoints to buy and sell assets. The server supports multiple cryptocurrency exchanges (Binance, CoinGecko) and stock trading platforms (BlackRock, Vanguard).

## Technology Stack

- **Java**: 21
- **Spring Boot**: 3.5.6
- **Spring Web**: RESTful web services
- **Spring WebFlux**: Reactive web client for API calls
- **Spring Actuator**: Application monitoring and management
- **Lombok**: Reduces boilerplate code
- **Maven**: Build and dependency management
- **Jackson**: JSON parsing

## Features

### Cryptocurrency Trading
- Buy cryptocurrencies with automatic quantity calculation based on investment amount
- Sell cryptocurrencies with automatic value calculation
- Real-time price fetching from CoinGecko API
- Automatic USD to INR conversion
- UUID-based coin tracking
- Support for multiple exchanges (Binance, CoinGecko)

### Stock Trading
- Buy stocks/shares with automatic quantity calculation
- Sell stocks/shares with value calculation
- Company name to stock symbol resolution
- Real-time stock price fetching from Alpha Vantage API
- Automatic USD to INR price conversion
- UUID-based share tracking
- Support for multiple trading platforms (BlackRock, Vanguard)

## Project Structure

```
TradingServer/
├── src/
│   ├── main/
│   │   ├── java/major/tradingserver/
│   │   │   ├── TradingServerApplication.java  # Main Spring Boot application
│   │   │   ├── ApiResponse.java               # Generic API response wrapper
│   │   │   ├── crypto/
│   │   │   │   ├── Binance.java               # Binance exchange REST controller
│   │   │   │   ├── CoinGekko.java             # CoinGecko exchange REST controller
│   │   │   │   ├── Coin.java                  # Cryptocurrency model
│   │   │   │   └── PriceFetch.java            # Crypto price fetching service
│   │   │   └── stock/
│   │   │       ├── BlackRock.java             # BlackRock trading REST controller
│   │   │       ├── Vanguard.java              # Vanguard trading REST controller
│   │   │       ├── Share.java                 # Stock/Share model
│   │   │       └── PriceFetchS.java           # Stock price fetching service
│   │   └── resources/
│   │       └── application.properties         # Application configuration
│   └── test/
│       └── java/major/tradingserver/
│           └── TradingServerApplicationTests.java
├── pom.xml                                    # Maven configuration
└── README.md
```

## Prerequisites

- Java 21 or higher
- Maven 3.6+
- Internet connection (for fetching real-time prices)

## Installation

1. Clone the repository:
```bash
git clone https://github.com/V-Satwik-Reddy/TradingServer.git
cd TradingServer
```

2. Build the project:
```bash
./mvnw clean install
```

3. Run the application:
```bash
./mvnw spring-boot:run
```

The server will start on `http://localhost:8080` by default.

## Configuration

The application uses the following configuration in `application.properties`:

```properties
spring.application.name=TradingServer
```

### API Keys

The stock trading functionality uses Alpha Vantage API. The API key is currently hardcoded in `PriceFetchS.java`:
```java
private final String apiKey = "RQLF8575GERSMQ75";
```

**Note**: For production use, you should externalize this to environment variables or application properties.

## API Endpoints

### Cryptocurrency Trading

#### Binance Exchange

**Base URL**: `/binance`

- **GET** `/binance/`
  - Welcome endpoint
  - Response: `"Welcome to Binance Server!"`

- **POST** `/binance/buy`
  - Buy cryptocurrency
  - Request Body:
    ```json
    {
      "coinName": "bitcoin",
      "price": 100000,
      "quantity": 0,
      "coinId": []
    }
    ```
  - Response: Returns purchased coin details with calculated quantity and unique IDs

- **POST** `/binance/sell`
  - Sell cryptocurrency
  - Request Body:
    ```json
    {
      "coinName": "bitcoin",
      "quantity": 0.5,
      "price": 0,
      "coinId": []
    }
    ```
  - Response: Returns sold coin details with calculated value

#### CoinGecko Exchange

**Base URL**: `/coingekko`

- **GET** `/coingekko/`
  - Welcome endpoint
  - Response: `"Welcome to Coingekko Server!"`

- **POST** `/coingekko/buy`
  - Buy cryptocurrency (same structure as Binance)

- **POST** `/coingekko/sell`
  - Sell cryptocurrency (same structure as Binance)

### Stock Trading

#### BlackRock Platform

**Base URL**: `/blackrock`

- **GET** `/blackrock/`
  - Welcome endpoint
  - Response: `"Welcome to Black Rock trading"`

- **POST** `/blackrock/buy`
  - Buy stocks/shares
  - Request Body:
    ```json
    {
      "companyName": "Apple",
      "price": 50000,
      "quantity": 0,
      "id": ""
    }
    ```
  - Response: Returns purchased share details with calculated quantity and unique ID

- **POST** `/blackrock/sell`
  - Sell stocks/shares
  - Request Body:
    ```json
    {
      "companyName": "Apple",
      "quantity": 10,
      "price": 0,
      "id": ""
    }
    ```
  - Response: Returns sold share details with calculated value

#### Vanguard Platform

**Base URL**: `/vanguard` (Note: Code shows `/blackrock` mapping but class is named Vanguard)

- **GET** `/vanguard/`
  - Welcome endpoint
  - Response: `"Welcome to Vanguard trading"`

- **POST** `/vanguard/buy`
  - Buy stocks/shares (same structure as BlackRock)

- **POST** `/vanguard/sell`
  - Sell stocks/shares (same structure as BlackRock)

## API Usage Examples

### Buying Cryptocurrency (Binance)

```bash
curl -X POST http://localhost:8080/binance/buy \
  -H "Content-Type: application/json" \
  -d '{
    "coinName": "bitcoin",
    "price": 100000,
    "quantity": 0,
    "coinId": []
  }'
```

Response:
```json
{
  "message": "Coin bought successfully",
  "data": {
    "coinName": "bitcoin",
    "quantity": 0.0015,
    "price": 6700000.50,
    "coinId": ["a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6"]
  },
  "status": 201
}
```

### Selling Cryptocurrency

```bash
curl -X POST http://localhost:8080/binance/sell \
  -H "Content-Type: application/json" \
  -d '{
    "coinName": "bitcoin",
    "quantity": 0.5,
    "price": 0,
    "coinId": []
  }'
```

### Buying Stocks (BlackRock)

```bash
curl -X POST http://localhost:8080/blackrock/buy \
  -H "Content-Type: application/json" \
  -d '{
    "companyName": "Apple",
    "price": 50000,
    "quantity": 0,
    "id": ""
  }'
```

Response:
```json
{
  "message": "shares successfully bought",
  "data": {
    "companyName": "Apple",
    "price": 18500.75,
    "quantity": 2.7,
    "id": "x9y8z7w6v5u4t3s2r1q0p9o8n7m6l5k4"
  },
  "status": 201
}
```

### Selling Stocks

```bash
curl -X POST http://localhost:8080/blackrock/sell \
  -H "Content-Type: application/json" \
  -d '{
    "companyName": "Apple",
    "quantity": 10,
    "price": 0,
    "id": ""
  }'
```

## External APIs Used

1. **CoinGecko API** (`https://api.coingecko.com`)
   - Used for fetching real-time cryptocurrency prices
   - Endpoint: `/api/v3/simple/price`
   - Parameters: `ids` (coin name), `vs_currencies` (usd)

2. **Alpha Vantage API** (`https://www.alphavantage.co`)
   - Used for fetching stock symbols and prices
   - Endpoints:
     - `SYMBOL_SEARCH`: Company name to stock symbol
     - `TIME_SERIES_DAILY`: Daily stock price data

3. **Currency API** (`https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest`)
   - Used for USD to INR conversion
   - Endpoint: `/v1/currencies/usd.json`

## Development

### Building from Source

```bash
./mvnw clean package
```

### Running Tests

```bash
./mvnw test
```

### Spring Boot Actuator

The application includes Spring Boot Actuator for monitoring and management. Access actuator endpoints at:
- `http://localhost:8080/actuator`

### Code Structure

- **Controllers**: Handle HTTP requests and responses
  - `Binance.java`, `CoinGekko.java` for crypto trading
  - `BlackRock.java`, `Vanguard.java` for stock trading
  
- **Services**: Business logic and external API integration
  - `PriceFetch.java` for cryptocurrency price fetching
  - `PriceFetchS.java` for stock price fetching
  
- **Models**: Data structures
  - `Coin.java` for cryptocurrency data
  - `Share.java` for stock data
  - `ApiResponse.java` for standardized API responses

### Price Calculation Logic

**Cryptocurrency Buy**:
- Fetch current price in USD from CoinGecko
- Convert to INR
- Calculate quantity: `investment_amount / (price_in_usd * usd_to_inr)`
- Generate unique IDs for each coin unit

**Cryptocurrency Sell**:
- Fetch current price in USD from CoinGecko
- Convert to INR
- Calculate return: `quantity * price_in_inr`

**Stock Buy**:
- Resolve company name to stock symbol
- Fetch latest closing price from Alpha Vantage
- Convert to INR
- Calculate quantity: `investment_amount / price_in_inr`
- Generate unique ID for the transaction

**Stock Sell**:
- Fetch latest stock price in INR
- Calculate return: `quantity * price_in_inr`

## License

This project is open source and available under the MIT License.

## Author

V-Satwik-Reddy

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
