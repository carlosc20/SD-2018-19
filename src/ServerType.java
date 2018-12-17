import java.time.LocalDateTime;
import java.util.Map;

public class ServerType {

    private final String id;    // Identificador
    private final int price;    // Preço por hora
    private final int total;    // Número máximo de instâncias disponíveis

    private int standardRes;
    private int auctionRes;
    private Map<Integer, Reservation> reservations;

    public ServerType(String id, int price, int total) {
        this.id = id;
        this.price = price;
        this.total = total;
    }

    // TODO: criar fila de espera(uma para cada tipo?) e concorrencia, tipo warehouse?


    public int getPrice() {
        return price;
    }

    /**
     *
     */
    public AuctionReservation addAuctionRes(String email, int bid) {

        AuctionReservation res = new AuctionReservation(email, this, LocalDateTime.now(), bid);

        if(standardRes == total) {                      // cheio
            // TODO: vai para fila
        } else if (standardRes + auctionRes == total) { // cheio mas tem reservas de leilao
            // TODO: substitui o mais barato se possível
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
    public StandardReservation addStandardRes(String email) {

        StandardReservation res = new StandardReservation(email, this, LocalDateTime.now());

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
