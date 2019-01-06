import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;

/**
 *  Representa uma reserva em leilão de um servidor.
 */
public class AuctionReservation extends Reservation implements Comparable<AuctionReservation> {

    private final int bid;

    /**
     * @see Reservation
     * @param bid a licitação associada á reserva, em cêntimos.
     */
    public AuctionReservation(ServerType serverType, User user, int bid) {
        super(serverType, user);
        this.bid = bid;
    }

    @Override
    public int getPrice() {
        return bid;
    }


    // TODO: 06/01/2019 nao aceder ao server
    /**
     * Cancela a reserva guardando o tempo de cancelamento e o montante total a pagar.
     * Deve ser usado em vez do {@link #cancel()} quando a reserva for cancelada para ser substituída por outra.
     */
    public void forceCancel() {
        this.getServerType().forceCancelRes(this);
        User user = this.getUser();
        user.cancelRes(this);
        this.setAmountDue(getAmountDue(LocalDateTime.now()));
        MainServer.canceledReservation(user.getEmail(), this.getId());
    }

    // TODO: 06/01/2019
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    /**
     * É comparado o preço e em caso de igualdade o id (ordem inversa).
     */
    @Override
    public int compareTo(AuctionReservation o) {
        int p1 = this.getPrice();
        int p2 = o.getPrice();
        if(p1 == p2) return Integer.compare(this.getId(), o.getId());
        return Integer.compare(p2, p1);
    }

}
