import java.util.List;

public class ServerType {

    private final String id; // Ex: "t3.micro", "m5.large"
    private final int price; //preço nominal fixo por hora
    private List<Reservation> reservations;

    public ServerType(String id, int price) {
        this.id = id;
        this.price = price;
    }
}
