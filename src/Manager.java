import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Manager {
    private static Manager ourInstance = new Manager();
    public static Manager getInstance() {
        return ourInstance;
    }

    private Map<String, User> users; // chave email
    private List<Reservation> reservations; // index id
    private Map<String, ServerType> servers; // chave id

    public Manager() {
        this.users = new HashMap<>();
        this.reservations = new ArrayList<>();
        this.servers = new HashMap<>();
    }

    /**
     * Regista um utilizador.
     *
     * @param email Email do novo utilizador
     * @param password Password do novo utilizador.
     */
    void registerUser(String email, String password) throws EmailJaExiste {

        if (users.containsKey(email))
            throw new EmailJaExiste(email);

        users.put(email, new User(email, password));
    }

    /**
     * autentica um utilizador.
     *
     * @param email Email do utilizador
     * @param password Password do utilizador.
     */
    void authenticateUser(String email, String password) throws EmailNaoExiste, SenhaErrada {

        User user = users.get(email);
        if (user == null) throw new EmailNaoExiste(email);
        if (!user.getPassword().equals(password)) throw new SenhaErrada(email);
        /*
            Código que guarda a sessão do utilizador
         */
    }


    /**
     * Reserva servidor de um tipo a pedido.
     *
     * @param email Email do utilizador que faz a reserva
     * @param serverType Tipo do servidor.
     * @return Id da reserva.
     */
    int createStandardReservation(String email, String serverType) {

        return 0;
    }


    /**
     * Reserva servidor de um tipo em leilao.
     *
     * @param email Email do utilizador que faz a reserva
     * @param serverType Tipo do servidor.
     * @param offer Preço pretendido.
     * @return Id da reserva.
    */
    int createAuctionReservation(String email, String serverType, int offer) {
        return 0;
    }


    void cancelReservation(String email, int id) {

    }


    int checkDebt(String email){
        return 0;
    }

}
