package major.tradingserver.crypto.binance;

import major.tradingserver.ApiResponse;
import major.tradingserver.crypto.Coin;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        double price=pf.getPrice(coin)*pf.usdInr();
        int quant= (int) Math.ceil((coin.getPrice()/price));
        List<String> coinId= new ArrayList<>();
        for(int i=0;i<quant;i++){
            coinId.add(UUID.randomUUID().toString().replace("-", ""));
        }
        Coin gencoin=new Coin(coin.getCoinName(),coin.getPrice()/price,price,coinId);
        ApiResponse<Coin> response =
                new ApiResponse<>("Coin bought successfully", gencoin, HttpStatus.CREATED.value());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PostMapping("/sell")
    public ResponseEntity<ApiResponse<Coin>> sellCoin(@RequestBody Coin coin) {
        double price=pf.getPrice(coin)*pf.usdInr();
        double r=price*coin.getQuantity();
        Coin sellcoin=new Coin(coin.getCoinName(),coin.getQuantity(),r,new ArrayList<>());
        ApiResponse<Coin> response= new ApiResponse<>("coin sold successfully", sellcoin, HttpStatus.CREATED.value());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
