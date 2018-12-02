import java.time.LocalDateTime;

public class AuctionReservation extends Reservation {

    private int bid;

    public AuctionReservation(String user, ServerType serverType, LocalDateTime start) {
        super(user, serverType, start);
    }
}
