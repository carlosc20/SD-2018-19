import java.time.LocalDateTime;

public class StandardReservation extends Reservation {

    public StandardReservation(String user, ServerType serverType, LocalDateTime start) {
        super(user, serverType, start);
    }
}