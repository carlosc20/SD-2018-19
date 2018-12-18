import java.time.LocalDateTime;
import static java.lang.Math.toIntExact;
import static java.time.temporal.ChronoUnit.HOURS;

/**
 *  Representa uma reserva de um servidor
 */
public abstract class Reservation {

    private static int lastId = 1;
    private final int id;
    private final ServerType serverType;
    private final LocalDateTime startTime;

    private int amountDue;          // é 0 até ser cancelada a reserva


    public Reservation(ServerType serverType, LocalDateTime startTime) {
        this.id = lastId++;
        this.serverType = serverType;
        this.startTime = startTime;
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


    /**
     * Calcula o montante a pagar associado à reserva tendo em conta como tempo de cancelamento o momento atual.
     * Para obter o montante de uma reserva já cancelada deve ser usado getAmountDue().
     *
     * @return  Preço em cêntimos.
     */
    public int getCurrentAmountDue() { // TODO: confirmar
        long hours = HOURS.between(startTime, LocalDateTime.now());
        long price = this.getPrice();

        return toIntExact(hours * price);
    }


    /**
     * Cancela a reserva guardando o tempo de cancelamento e o montante total a pagar.
     */
    public void cancel() {
        amountDue = getCurrentAmountDue();
    }


    /**
     * Devolve o preço por hora associado à reserva.
     *
     * @return  Preço em cêntimos.
     */
    public abstract int getPrice();
}

