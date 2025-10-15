package major.tradingserver.crypto;

import major.tradingserver.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/binance")
public class Binance {
    private final PriceFetch pf;
    public Binance(PriceFetch pf) {
        this.pf = pf;
    }
    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Welcome to Binance Server!");
    }

    @PostMapping("/buy")
    public ResponseEntity<ApiResponse<Coin>> buyCoin(@RequestBody Coin coin) {
        Coin rcoin=coin;
        double price=pf.getPrice(coin);
//        System.out.println();
        rcoin.setPrice(price);
        ApiResponse<Coin> response =
                new ApiResponse<>("Coin bought successfully", rcoin, HttpStatus.CREATED.value());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


}
