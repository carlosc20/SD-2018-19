import java.time.LocalDateTime;
import java.util.*;

public class User {

    private final String email;
    private final String password;
    private final Map<Integer, Reservation> activeRes;
    private final List<Reservation> canceledRes;
    private final List<Integer> canceledAuctionResIds;


    public User(String email, String password) {
        this.password = password;
        this.activeRes = new HashMap<>();
        this.canceledRes = new ArrayList<>();
        this.canceledAuctionResIds = new ArrayList<>();
        this.email = email;
    }

    public String getEmail(){
        return this.email;
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
        LocalDateTime now = LocalDateTime.now();
        synchronized (activeRes) {
                for (Reservation res : canceledRes) {
                    total += res.getAmountDue();
                }

                for (Reservation res : activeRes.values()) {
                    total += res.getAmountDue(now);
                }
                return total;
        }
    }


    /**
     * Adiciona uma reserva ao utilizador.
     *
     * @param res a reserva a adicionar.
     */
    public void addReservation(Reservation res) {
        synchronized (activeRes) {
            activeRes.put(res.getId(), res);
        }
    }


    /**
     * Passa uma reserva da lista de ativas para as canceladas.
     *
     * @param res a reserva a cancelar.
     */
    public void cancelRes(Reservation res) {
        synchronized (activeRes) {
            activeRes.remove(res.getId());
            canceledRes.add(res);
        }
    }


    /**
     * Devolve a reserva não cancelada correspondente ao id fornecido.
     *
     * @param  id o id da reserva.
     * @return reserva correspondente ao id.
     */
    public Reservation getActiveReservation(int id) {
        return  activeRes.get(id);
    }


    /**
     * Adiciona o id de uma reserva cancelada enquanto o utilizador estava desconectado.
     *
     * @param id o id da reserva.
     */
    public void addCanceledAuctionReservation(int id) {
        synchronized (canceledAuctionResIds) {
            canceledAuctionResIds.add(id);
        }
    }


    /**
     * Devolve uma lista de ids das reservas canceladas enquanto o utilizador estava desconectado e limpa a lista.
     *
     * @return lista de ids das reservas.
     */
    public List<Integer> popCanceledAuctionReservations() {
        synchronized (canceledAuctionResIds) {
            List<Integer> list = new ArrayList<>(canceledAuctionResIds);
            canceledAuctionResIds.clear();
            return list;
        }
    }



}
