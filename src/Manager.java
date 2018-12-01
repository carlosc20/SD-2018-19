import java.util.List;
import java.util.Map;

public class Manager {

    private Map<String, User> users;
    private List<Reservation> reservations;
    private Map<String, Server> servers;

    void registerUser(String email, String password) throws Exception {

        if (users.containsKey(email))
            throw new Exception();

        users.put(email, new User(email, password));
    }


    boolean authenticateUser(String email, String password) throws Exception {

        User user = users.get(email);
        if (user == null) throw new Exception(); // ?

        boolean check = false;
        if (user.getPassword().equals(password))
            check = true;

        return check;
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
