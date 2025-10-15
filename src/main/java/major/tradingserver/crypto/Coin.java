package major.tradingserver.crypto;

import lombok.Data;

@Data
public class Coin {
    private String coinName;
    private double quantity;
    private double price;
    private String coinId;
}
