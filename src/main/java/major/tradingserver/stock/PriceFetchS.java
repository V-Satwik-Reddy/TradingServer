package major.tradingserver.stock;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Iterator;
import java.util.Map;

@Service
public class PriceFetchS {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String apiKey = "RQLF8575GERSMQ75"; // put your API key here
    private static final String API_URL = "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/usd.json";

    public PriceFetchS(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://www.alphavantage.co").build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 1️⃣ Get the symbol of a stock given its company name
     */
    public String getSymbol(String companyName) throws Exception {
        String url = "/query?function=SYMBOL_SEARCH&keywords=" + companyName + "&apikey=" + apiKey;

        String response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .block();
//        System.out.println(url);
        JsonNode root = objectMapper.readTree(response);
        JsonNode bestMatches = root.path("bestMatches");
        if (bestMatches.isArray() && bestMatches.size() > 0) {
            return bestMatches.get(0).path("1. symbol").asText();
        }

        throw new RuntimeException("Symbol not found for company: " + companyName);
    }

    /**
     * 2️⃣ Get the latest price of a stock given its company name
     */
    public double getPrice(String companyName) throws Exception {
        // First get the symbol
        String symbol = getSymbol(companyName);

        // Hit TIME_SERIES_INTRADAY (or TIME_SERIES_DAILY if you want daily)
        String url = "/query?function=TIME_SERIES_DAILY&symbol=" + symbol + "&apikey=" + apiKey;
//        System.out.println(url);
        String response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        JsonNode root = objectMapper.readTree(response);
        JsonNode timeSeries = root.path("Time Series (Daily)");

        if (timeSeries.isMissingNode()) {
            throw new RuntimeException("Time Series not found for symbol: " + symbol);
        }

        // Get the latest date (first key in the JSON object)
        Iterator<String> dates = timeSeries.fieldNames();
        if (!dates.hasNext()) throw new RuntimeException("No data available");
        String latestDate = dates.next();

        JsonNode latestData = timeSeries.path(latestDate);
        double latestClose = latestData.path("4. close").asDouble();

        return usdInr()*latestClose;
    }

    public double usdInr() {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(API_URL, Map.class);
        Map<String, Double> usdRates = (Map<String, Double>) response.get("usd");
        return usdRates.get("inr");
    }
}
