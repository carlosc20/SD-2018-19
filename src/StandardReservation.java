/**
 *  Representa uma reserva standard de um servidor.
 */
public class StandardReservation extends Reservation {

    public StandardReservation(ServerType serverType, User user) {
        super(serverType, user);
    }

    @Override
    public int getPrice() {
        return this.getServerType().getPrice();
    }
}
