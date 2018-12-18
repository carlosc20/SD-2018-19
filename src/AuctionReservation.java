import java.time.LocalDateTime;


/**
 *  Representa uma reserva em leilão de um servidor, pode ser cancelada pelo utilizador que a criou ou para dar lugar a uma reserva com oferta melhor ou standard.
 */
public class AuctionReservation extends Reservation {

    private final int bid;

    public AuctionReservation(ServerType serverType, LocalDateTime startTime, int bid) {
        super(serverType, startTime);
        this.bid = bid;
    }

    @Override
    public int getPrice() {
        return bid;
    }
}
