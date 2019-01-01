import java.time.LocalDateTime;

/**
 *  Representa uma reserva em leilão de um servidor, pode ser cancelada pelo utilizador que a criou ou para dar lugar a uma reserva com oferta melhor ou standard.
 */
public class AuctionReservation extends Reservation implements Comparable {

    private final int bid;

    public AuctionReservation(ServerType serverType, User user, int bid) {
        super(serverType, user);
        this.bid = bid;
    }

    @Override
    public int getPrice() {
        return bid;
    }


    /**
     *
     */
    @Override
    public int compareTo(Object o) {
        AuctionReservation that = (AuctionReservation) o;
        int p1 = this.getPrice();
        int p2 = that.getPrice();
        if(p1 == p2) return Integer.compare(this.getId(), that.getId());
        return Integer.compare(p1, p2);
    }

    /**
     * Cancela a reserva guardando o tempo de cancelamento e o montante total a pagar.
     */
    public void forceCancel() {
        this.getServerType().forceCancelRes(this);
        this.getUser().cancelRes(this);
        this.setAmountDue(getAmountDue(LocalDateTime.now()));
    }

}
