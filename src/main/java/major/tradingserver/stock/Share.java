package major.tradingserver.stock;


import lombok.Data;
@Data
public class Share {
    private String companyName;
    private double price;
    private double quantity;
    private String id;
    public Share(String companyName, double price,double quantity, String id) {
        this.companyName = companyName;
        this.price = price;
        this.quantity = quantity;
        this.id = id;
    }
}
