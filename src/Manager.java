import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class Manager {

    private Map<String, User> users; // chave email
    private List<Reservation> reservations; // index id
    private Map<String, ServerType> servers; // chave id

    public Manager(Map<String, ServerType> servers) {
        this.servers = servers;
    }

    void registerUser(String email, String password) throws Exception {

        if (users.containsKey(email))
            throw new Exception();

        users.put(email, new User(email, password));
    }

    /**
     * Verifica credenciais de um utilizador.
     *
     * @return true se as credenciais se verificarem.
     */
    boolean authenticateUser(String email, String password) throws Exception {

        User user = users.get(email);
        if (user == null) throw new Exception(); // ?

        boolean isValid = false;
        if (user.getPassword().equals(password))
            isValid = true;

        return isValid;
    }


    /**
     * Reserva servidor de um tipo a pedido.
     *
     * @return Id da reserva.
     */
    // request, on demand, a pedido
    /*
    ve servidores do tipo disponiveis
    ve reservas de leilao e cancela
     */
    int createStandardReservation(String email, String serverType) {

        ServerType st = servers.get(serverType);
        StandardReservation res = new StandardReservation(email, st, LocalDateTime.now());



        return res.getId();
    }


    /**
     * Reserva servidor de um tipo em leilão.
     *
     * @return Id da reserva.
    */
    /*
    Ve se tem livres do tipo
    ve se tem reservas de leilao mais baratas e cancela
    fica em espera
     */
    int createAuctionReservation(String email, String serverType, int offer) {
        return 0;
    }

    /**
     * Cancela uma reserva
     *
     * @param id Id da reserva.
     */
    void cancelReservation(String email, int id) throws Exception {

        Reservation res = reservations.get(id);
        if(res == null) throw new Exception();

        User user = users.get(email);
        user.cancelRes(id);

        res.getServerType().cancelRes(id);
    }

    /**
     * Calcula o valor em dívida atual.
     * Este valor é calculado a partir da soma dos valores de todas as reservas canceladas e dos valores
     * acumulados até ao momento das reservas atuais.
     *
     * @return Dívida total acumulada em cêntimos.
     */
    int checkDebt(String email){

        int totalDue = 0;

        List<Reservation> resList = users.get(email).getCanceledRes();
        for (Reservation res : resList) {
            totalDue += res.getAmountDue();
        }

        // TODO: calcular o total das reservas atuais

        return totalDue;
    }

}
