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
    private final User user;
    private LocalDateTime startTime;
    private int amountDue;


    /**
     * Construtor com um determinado tipo e utilizador.
     * O id é atribuído automaticamente, começando em 1 e sendo incrementado em cada reserva nova.
     *
     * @param serverType o tipo do servidor da reserva.
     * @param user o utilizador que tem a reserva.
     */
    public Reservation(ServerType serverType, User user) {
        this.id = lastId++;
        this.serverType = serverType;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public User getUser() {
        return user;
    }

    public int getAmountDue() {
        return amountDue;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setAmountDue(int amountDue) {
        this.amountDue = amountDue;
    }


    /**
     * Calcula o montante a pagar associado à reserva tendo em conta como tempo de cancelamento o tempo passado
     * como parâmetro.
     * Para obter o montante de uma reserva já cancelada deve ser usado {@link #getAmountDue()}.
     *
     * @param  endTime o tempo final usado para cálculo da dívida.
     * @return montante a pagar em cêntimos, 0 se o tempo for antes da criação da reserva.
     */
    public int getAmountDue(LocalDateTime endTime) {
        if(endTime.isBefore(startTime)) return 0;
        long hours = HOURS.between(startTime, endTime); // TODO: 06/01/2019 Verificar
        int price = this.getPrice();
        return toIntExact(hours * price);
    }


    /**
     * Cancela a reserva guardando o tempo de cancelamento e o montante total a pagar.
     * Deve ser apenas usado a pedido do utilizador a que pertence a reserva.
     */
    public void cancel() {
        serverType.cancelRes(this);
        user.cancelRes(this);
        amountDue = getAmountDue(LocalDateTime.now());
    }


    /**
     * Devolve o preço por hora associado à reserva.
     *
     * @return preço em cêntimos.
     */
    public abstract int getPrice();
}

