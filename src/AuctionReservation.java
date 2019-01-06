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


    /**
     * Cancela a reserva guardando o tempo de cancelamento e o montante total a pagar.
     * Deve ser usado em vez do {@link #cancel()} quando a reserva for cancelada para ser substituída por outra.
     * Notifica o utilizador de que a reserva foi cancelada.
     */
    public void forceCancel() throws InterruptedException {
        this.getServerType().forceCancelRes(this);
        User user = this.getUser();
        user.cancelRes(this);
        this.setAmountDue(getAmountDue(LocalDateTime.now()));
        MainServer.canceledReservation(user.getEmail(), this.getId());
    }

    /**
     * Usa os ids como critério de igualdade.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        AuctionReservation res = (AuctionReservation) obj;
        return this.getId() == res.getId();
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
