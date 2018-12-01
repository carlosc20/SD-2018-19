import java.time.LocalDateTime;

public class StandardReservation extends Reservation {

    public StandardReservation(String user, Server server, LocalDateTime start) {
        super(user, server, start);
    }
}
