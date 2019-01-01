import java.time.LocalDateTime;
import java.util.*;

public class User {

    private final String password;
    private Map<Integer, Reservation> activeRes;
    private List<Reservation> canceledRes;

    public User(String password) {
        this.password = password;
        this.activeRes = new HashMap<>();
        this.canceledRes = new ArrayList<>();
    }

    public String getPassword() {
        return password;
    }

    /**
     *  Calcula o total devido atual de reservas de servidores.
     *
     *  @return Total devido em cêntimos.
     */
    synchronized public int getTotalDue() {
        int total = 0;
        for (Reservation res : canceledRes) {
            total += res.getAmountDue();
        }

        LocalDateTime now = LocalDateTime.now();
        // TODO: 01/01/2019 é preciso concorrencia?
        /*
        Collection<Reservation> active = activeRes.values();
        synchronized (activeRes) {
            for (Reservation res : active) {
                res.lock.lock();
            }
        }
        for (Reservation res : active) {
            total += res.getAmountDue(now);
            res.lock.unlock();
        }
        */
        for (Reservation res : activeRes.values()) {
            total += res.getAmountDue(now);
        }
        return total;
    }


    /**
     * Passa uma reserva da lista de ativas para as canceladas.
     */
    public void cancelRes(Reservation res) {
        activeRes.remove(res.getId());
        canceledRes.add(res);
    }


    /**
     * Adiciona uma reserva ao utilizador.
     */
    public void addReservation(Reservation res) {
        activeRes.put(res.getId(), res);
    }


    /**
     * Devolve a reserva não cancelada correspondente ao id fornecido.
     */
    public Reservation getActiveReservation(int id) {
        return  activeRes.get(id);
    }
}
