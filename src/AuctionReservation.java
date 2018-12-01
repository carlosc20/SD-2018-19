import java.time.LocalDateTime;

public class AuctionReservation extends Reservation {

    private int bid;

    public AuctionReservation(String user, Server server, LocalDateTime start) {
        super(user, server, start);
    }
}
