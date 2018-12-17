import java.time.LocalDateTime;

public class AuctionReservation extends Reservation {

    private final int bid;

    public AuctionReservation(String userEmail, ServerType serverType, LocalDateTime startTime, int bid) {
        super(userEmail, serverType, startTime);
        this.bid = bid;
    }

    @Override
    public int getPrice() {
        return bid;
    }
}
