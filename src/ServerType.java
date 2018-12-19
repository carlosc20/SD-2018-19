import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 *  Representa um conjunto de servidores do mesmo tipo
 */
public class ServerType {

    private final int price;    // Preço fixo por hora
    private final int total;    // Número fixo máximo de instâncias disponíveis

    private Map<Integer, Reservation> reservations; //chave id

    private int standardRes;    // Número de instâncias ocupadas com reservas standard
    private int auctionRes;     // Número de instâncias ocupadas com reservas de leilão
    private PriorityQueue<Reservation> queue;


    public ServerType(int price, int total) {
        this.price = price;
        this.total = total;
        reservations = new HashMap<>();
        queue = new PriorityQueue<>(); // passar comparador
    }


    // TODO: criar fila de espera(uma para cada tipo?) e concorrencia

    public int getPrice() {
        return price;
    }


    /**
     *
     */
    public AuctionReservation addAuctionRes(int bid) {

        AuctionReservation res = new AuctionReservation(this, LocalDateTime.now(), bid);

        if(standardRes == total) {                      // cheio
            queue.add(res);
        } else if (standardRes + auctionRes == total) { // cheio mas tem reservas de leilao
            // TODO: substitui o mais barato se possível, senao vai para fila
            reservations.put(res.getId(), res);
        } else {                                        // tem servidores livres
            reservations.put(res.getId(), res);
            auctionRes++;
        }

        return res;
    }


    /**
     *
     */
    public StandardReservation addStandardRes() {

        StandardReservation res = new StandardReservation(this, LocalDateTime.now());

        if(standardRes == total) {                      // cheio
            // TODO: vai para fila
        } else if (standardRes + auctionRes == total) { // cheio mas tem reservas de leilao
            // TODO: substitui o auction mais barato
            reservations.put(res.getId(), res);
            standardRes++;
            auctionRes--;
        } else {                                        // tem servidores livres
            reservations.put(res.getId(), res);
            standardRes++;
        }

        return res;
    }


    /**
     *
     */
    public void cancelRes(int id) {
        Reservation res = reservations.get(id);
        if (res instanceof StandardReservation)
            standardRes--;
        else
            auctionRes--;

        reservations.remove(id);

        // TODO: avisa que ha espaços livres
    }
}
