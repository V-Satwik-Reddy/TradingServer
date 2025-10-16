package major.tradingserver.stock;

import major.tradingserver.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("blackrock")
public class Vanguard {
    PriceFetchS pf;
    public Vanguard(major.tradingserver.stock.PriceFetchS pf) {
        this.pf = pf;
    }

    @GetMapping("/")
    public ResponseEntity<String> getPrice() {
        return new ResponseEntity<>("Welcome to Vanguard trading", HttpStatus.OK);
    }

    @PostMapping("/buy")
    public ResponseEntity<ApiResponse<Share>> buyShare(@RequestBody Share share) throws Exception {
        String companyName = share.getCompanyName();
        double price=pf.getPrice(companyName);
        double quant=share.getPrice()/price;
        String id= UUID.randomUUID().toString().replace("-", "");
        Share s=new Share(companyName,price,quant,id);
        ApiResponse<Share> apiResponse=new ApiResponse<>("shares successfully bought",s,HttpStatus.CREATED.value());
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
    @PostMapping("/sell")
    public ResponseEntity<ApiResponse<Share>> sellShare(@RequestBody Share share) throws Exception {
        String companyName = share.getCompanyName();
        double price=pf.getPrice(companyName);
        double quant=share.getQuantity();
        Share s=new Share(companyName,price*quant,quant,"");
        ApiResponse<Share> apiResponse=new ApiResponse<>("shares sold bought",s,HttpStatus.CREATED.value());
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
}
