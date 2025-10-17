# Trading Server (Spring Boot)

A lightweight Spring Boot "Trading Server" that simulates cryptocurrency and stock buy/sell endpoints. This app is intended to be used as a reproducible replacement for real trading platforms and realtime APIs when testing AI agents or automated systems — i.e., when you can't or shouldn't hit live exchanges/APIs during tests.

It exposes simple REST endpoints for:
- Crypto: simulated Binance and CoinGecko-style behavior
- Stocks: simulated brokers (BlackRock and Vanguard endpoints)

> Important: The application code attempts to call public APIs (CoinGecko, AlphaVantage and a currency conversion JSON). In constrained testing environments where external network calls aren't allowed (or when you don't want to use real trading systems), run and use this server as the substitute. You can also mock or stub the PriceFetch/PriceFetchS services for offline testing.

## Highlights / Features
- REST controllers for crypto and stock operations
    - Crypto controllers: `/binance` and `/coingekko`
    - Stock controllers: `/blackrock` (used by both BlackRock and Vanguard classes)
- Endpoints:
    - GET `/` (health / welcome)
    - POST `/buy` — buy simulation
    - POST `/sell` — sell simulation
- Simple domain classes for responses:
    - Coin (coinName, quantity, price, coinId[])
    - Share (companyName, price, quantity, id)
- Price fetching helpers:
    - `PriceFetch` (crypto) — uses CoinGecko + currency conversion API
    - `PriceFetchS` (stocks) — uses AlphaVantage (requires API key) + currency conversion API
- Responses are wrapped in ApiResponse<T> with message, data and status code.

## Build & Run

Prerequisites
- Java 11+ (or the Java version required by your project setup)
- Maven (or use the Maven wrapper `./mvnw` if present)
- Network access if you want the service to call external price APIs

Run with Maven:
- From the repository root:
    - mvn clean package
    - java -jar target/<your-artifact>.jar
    - or simply: mvn spring-boot:run

If the repo includes a `./mvnw` wrapper:
- ./mvnw spring-boot:run

The server starts on the configured Spring Boot port (default 8080).

## API: Crypto (Binance / CoinGekko)
Base paths:
- /binance
- /coingekko

Both controllers implement the same behavior (buy/sell).

1) POST /binance/buy (or /coingekko/buy)
- Request body: Coin JSON
    - coinName: string (CoinGecko id, e.g. "bitcoin" or "ethereum")
    - price: number — amount you want to spend (the controller treats this as the amount in INR to spend)
- Behavior:
    - PriceFetch gets the coin's USD price via CoinGecko, converts to INR using currency API.
    - Quantity purchased = ceil(request.price / currentPriceInINR)
    - Generates unique IDs for each unit purchased
- Response: ApiResponse<Coin> with Coin containing:
    - coinName
    - quantity (computed)
    - price (unit price in INR)
    - coinId (list of generated IDs)

Example request:
{
"coinName": "bitcoin",
"price": 100000.0
}

2) POST /binance/sell (or /coingekko/sell)
- Request body: Coin JSON
    - coinName: string
    - quantity: number (quantity to sell)
- Behavior:
    - PriceFetch computes unit price in INR, multiplies by quantity to return INR amount received
- Response: ApiResponse<Coin> with Coin where:
    - price contains the total INR value received
    - quantity is the quantity sold
    - coinId is an empty array

Example request:
{
"coinName": "bitcoin",
"quantity": 0.001
}

## API: Stocks (BlackRock / Vanguard)
Base path: /blackrock
> Note: In current code both BlackRock and Vanguard controllers are annotated with `@RequestMapping("blackrock")`. If you intend to expose proper separate routes, adjust Vanguard's mapping.

1) POST /blackrock/buy
- Request body: Share JSON
    - companyName: full company name (used to search symbol)
    - price: numeric — amount you want to spend (INR)
- Behavior:
    - PriceFetchS uses AlphaVantage to find a symbol then gets the latest close price (in USD), converts to INR
    - quantity = request.price / marketPriceINR
    - a random id is generated
- Response: ApiResponse<Share> with Share containing:
    - companyName
    - price: current market price (INR)
    - quantity: computed
    - id: generated id

Example request:
{
"companyName": "Apple",
"price": 100000.0
}

2) POST /blackrock/sell
- Request body: Share JSON
    - companyName
    - quantity: number (quantity to sell)
- Behavior:
    - PriceFetchS gets current market price (INR) and total returned = price * quantity
- Response: ApiResponse<Share> where price contains the total INR value returned and id is empty.

Example request:
{
"companyName": "Apple",
"quantity": 2.5
}

## Models

Coin
- coinName: String
- quantity: double
- price: double (semantic varies: unit price for buy responses, total INR for sell response)
- coinId: List<String>

Share
- companyName: String
- price: double (market unit price in INR for buy response, total INR for sell response)
- quantity: double
- id: String

ApiResponse<T>
- message: String
- data: T
- status: int

## Important Notes / Caveats
- External APIs:
    - Crypto: CoinGecko (api.coingecko.com)
    - Currency conversion: cdn.jsdelivr.net package returning USD conversion rates to INR
    - Stocks: AlphaVantage (www.alphavantage.co) — PriceFetchS contains a hard-coded placeholder `apiKey = "RQLF8575GERSMQ75"`. Replace with your real API key or inject it via configuration/environment.
- Blocking calls: WebClient's Mono is blocked with .block() in the price fetchers for simplicity. Consider async/reactive handling if you need production-level behavior.
- Error handling: Minimal. Several operations throw raw runtimes or print stack traces. Add proper exception handling for robustness.
- Request semantics:
    - For buy endpoints the request "price" is interpreted as the amount you want to spend (INR). For sell endpoints send "quantity" to sell.
    - This apparent dual use of the "price" field in incoming objects can be confusing — consider adding dedicated DTOs for buy vs sell requests.
- Routes:
    - Vanguard controller currently uses `@RequestMapping("blackrock")`, causing a route collision. Rename to `/vanguard` if intended.
- For testing agents you likely want to:
    - Disable or mock external API calls (PriceFetch / PriceFetchS) and return deterministic prices.
    - Seed deterministic IDs if reproducibility is required.

## Example curl
Buy bitcoin on Binance (spend 100000 INR):
curl -X POST http://localhost:8080/binance/buy -H "Content-Type: application/json" -d '{"coinName":"bitcoin","price":100000.0}'

Sell 0.01 bitcoin:
curl -X POST http://localhost:8080/binance/sell -H "Content-Type: application/json" -d '{"coinName":"bitcoin","quantity":0.01}'

Buy shares (BlackRock):
curl -X POST http://localhost:8080/blackrock/buy -H "Content-Type: application/json" -d '{"companyName":"Microsoft","price":50000.0}'

Sell shares:
curl -X POST http://localhost:8080/blackrock/sell -H "Content-Type: application/json" -d '{"companyName":"Microsoft","quantity":1.5}'

## Suggested Improvements
- Extract request DTOs for buy vs sell to avoid reuse of fields with different meanings.
- Externalize AlphaVantage API key to application.properties or environment variables.
- Add unit and integration tests; mock external HTTP calls with WireMock or MockWebServer for deterministic test runs.
- Fix route collisions (Vanguard mapping).
- Improve error handling and return appropriate HTTP error codes when external APIs fail or input validation fails.

## Contributing
Contributions welcome. If you plan to use this as a testbed for agents:
- Add dedicated deterministic price providers for offline testing.
- Add endpoints to seed mocked responses for PriceFetch and PriceFetchS.

---

This README should help you (and your agents) use this Spring app as a safe replacement for live trading and realtime price APIs in tests. If you want, I can:
- produce example Postman collection,
- add a small mocked PriceFetch implementation and tests,
- or prepare a `docker-compose` file to run locally with a mock server.