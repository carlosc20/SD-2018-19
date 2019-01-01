import java.time.LocalDateTime;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Math.toIntExact;
import static java.time.temporal.ChronoUnit.HOURS;

/**
 *  Representa uma reserva de um servidor
 */
public abstract class Reservation implements Comparable {

    private static int lastId = 1;
    private final int id;
    private final ServerType serverType;
    private final User user;
    private LocalDateTime startTime;
    public ReentrantLock lock; // por public, sinceramente até nem fica mal.

    private int amountDue;          // é 0 até ser cancelada a reserva


    public Reservation(ServerType serverType, User user) {
        this.id = lastId++;
        this.serverType = serverType;
        this.user =user;
    }

    public int getId() {
        return id;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public int getAmountDue() {
        return amountDue;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }


    /**
     * Calcula o montante a pagar associado à reserva tendo em conta como tempo de cancelamento o tempo passado
     * como parâmetro.
     * Para obter o montante de uma reserva já cancelada deve ser usado getAmountDue().
     *
     * @param time tempo usado para cálculo da dívida
     *
     * @return  Preço em cêntimos.
     */
    public int getAmountDue(LocalDateTime time) { // TODO: confirmar
        if(time.isBefore(startTime)) return 0;
        long hours = HOURS.between(startTime, time);
        long price = this.getPrice();

        return toIntExact(hours * price);
    }


    /**
     * Cancela a reserva guardando o tempo de cancelamento e o montante total a pagar.
     */
    public void cancel() {
        serverType.cancelRes(this);
        user.cancelRes(this);
        amountDue = getAmountDue(LocalDateTime.now());
    }


    /**
     * Devolve o preço por hora associado à reserva.
     *
     * @return  Preço em cêntimos.
     */
    public abstract int getPrice();

    @Override
    public int compareTo(Object o) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        Reservation that = (Reservation) o; // TODO: ??

        if (this == that) return EQUAL;

        if (this instanceof StandardReservation && that instanceof StandardReservation)
            return Integer.compare(this.getId(), that.getId());

        if(this instanceof StandardReservation)
            return BEFORE;

        if(that instanceof StandardReservation)
            return AFTER;

        AuctionReservation o1 = (AuctionReservation) this;
        AuctionReservation o2 = (AuctionReservation) that;

        return o1.compareTo(o2);
    } // TODO: melhorar
}

