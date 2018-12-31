import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {

    private final String password;
    private Map<Integer, Reservation> currentRes;
    private List<Reservation> canceledRes;

    public User(String password) {
        this.password = password;
        this.currentRes = new HashMap<>();
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
    public int getTotalDue() {
        int total = 0;

        for (Reservation res : canceledRes) {
            total += res.getAmountDue();
        }
        for (Reservation res : currentRes.values()) {
            total += res.getCurrentAmountDue();
        }

        return total;
    }


    /**
     * Passa uma reserva da lista de atuais para as canceladas.
     */
    public void cancelRes(Reservation res) {
        currentRes.remove(res.getId());
        canceledRes.add(res);
    }


    /**
     * Adiciona uma reserva ao utilizador.
     */
    public void addReservation(Reservation res) {
        currentRes.put(res.getId(), res);
    }


    /**
     * Devolve a reserva não cancelada correspondente ao id fornecido.
     */
    public Reservation getCurrentRes(int id) throws Exception {
        Reservation res = currentRes.get(id);
        if (res == null) throw new Exception();
        return res;
    }
}
