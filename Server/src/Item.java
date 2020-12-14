import java.util.ArrayList;

public class Item {
    String highestBidder;
    Boolean sold;
    String name;
    String messName;
    String description;
    Double bidPrice;
    Double buyPrice;
    ArrayList<String> history;

    public Item(String name, String description, Double bidPrice) {
        this.sold = false;
        this.name = name;
        this.description = description;
        this.bidPrice = bidPrice;
    }
}