package major.tradingserver.crypto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PriceFetch {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

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
}
