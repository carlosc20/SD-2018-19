import java.util.List;
import java.util.Map;

public class ServerType {

    private final String id;            // Identificador
    private final int price;            // Preço por hora
    private final int totalInstances;   // Número máximo de instâncias disponíveis

    private int currentInstances;
    private Map<Integer, Reservation> reservations;

    public ServerType(String id, int price, int totalInstances) {
        this.id = id;
        this.price = price;
        this.totalInstances = totalInstances;
    }

    public void cancelRes(int id) {
        reservations.remove(id);

        // TODO: avisa que ha espaços livres
    }
}
