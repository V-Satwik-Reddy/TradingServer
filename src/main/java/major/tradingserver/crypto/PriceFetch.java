package major.tradingserver.crypto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class PriceFetch {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private static final String API_URL = "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/usd.json";
    public PriceFetch(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.coingecko.com").build();
        this.objectMapper = new ObjectMapper();
    }

    public double getPrice(Coin coin) {
        String coinName = coin.getCoinName(); // dynamic insertion

        // Call CoinGecko API
        Mono<String> responseMono = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v3/simple/price")
                        .queryParam("ids", coinName)
                        .queryParam("vs_currencies", "usd")
                        .build())
                .retrieve()
                .bodyToMono(String.class);

        String response = responseMono.block(); // blocking for simplicity

        try {
            // Parse JSON
            JsonNode root = objectMapper.readTree(response);
            // Extract the USD price
            return root.get(coinName).get("usd").asDouble();
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0; // fallback if parsing fails
        }
    }
    public double usdInr() {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(API_URL, Map.class);
        Map<String, Double> usdRates = (Map<String, Double>) response.get("usd");
        return usdRates.get("inr");
    }
}
