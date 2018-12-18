import java.time.LocalDateTime;


/**
 *  Representa uma reserva standard de um servidor, deve ser apenas cancelada pelo utilizador que a criou.
 */
public class StandardReservation extends Reservation {

    public StandardReservation(ServerType serverType, LocalDateTime start) {
        super(serverType, start);
    }

    @Override
    public int getPrice() {
        return this.getServerType().getPrice();
    }
}
