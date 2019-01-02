import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;

/**
 *  Facade da lógica
 */
public class Manager implements ManagerInterface {

    private static Manager ourInstance = new Manager();

    private final Map<String, User> users;         // chave email
    private final Map<String, ServerType> servers; // chave id


    private Manager() {
        this.users = new HashMap<>();
        this.servers = new HashMap<>();
        // Exemplo:
        servers.put("cinco", new ServerType(300, 5));
        servers.put("tres", new ServerType(600, 3));
        servers.put("um", new ServerType(500, 1));
    }

    public static Manager getInstance() {
        return ourInstance;
    }


    /**
     * Regista um utilizador.
     * Usa o lock do users para impedir registos com o mesmo email.
     *
     * @param email Email do novo utilizador
     * @param password Password do novo utilizador.
     */
    public void registerUser(String email, String password) throws EmailAlreadyUsedException {
        synchronized (users) {
            if (users.containsKey(email)) {
                throw new EmailAlreadyUsedException(email);
            }
            users.put(email, new User(email, password));
        }
    }


    /**
     * Verifica credenciais de um utilizador.
     *
     * @return true se as credenciais se verificarem.
     */
    public boolean checkCredentials(String email, String password) {
        User user = users.get(email);
        return (user != null && user.getPassword().equals(password));
    }


    /**
     * Cria reserva standard de um servidor de um tipo.
     *
     * @return Id da reserva.
     */
    public int createStandardReservation(String email, String serverType) throws ServerTypeDoesntExistException {

        ServerType st = servers.get(serverType);
        if(st == null) throw new ServerTypeDoesntExistException();

        User user = users.get(email);
        StandardReservation res = st.addStandardRes(user); // espera até ser atribuída
        user.addReservation(res);
        
        return res.getId();
    }


    /**
     * Cria reserva de leilão de um servidor de um tipo.
     *
     * @param bid Valor positivo diferente de 0.
     * @return Id da reserva.
    */
    public int createAuctionReservation(String email, String serverType, int bid) throws ServerTypeDoesntExistException {

        if(bid <= 0) throw new IllegalArgumentException(); // TODO: 01/01/2019 exception?

        ServerType st = servers.get(serverType);
        if(st == null) throw new ServerTypeDoesntExistException();

        User user = users.get(email);
        AuctionReservation res = st.addAuctionRes(user, bid); // espera até ser atribuída
        user.addReservation(res);

        return res.getId();
    }


    /**
     * Cancela uma reserva
     *
     */
    public void cancelReservation(String email, int id) throws ReservationDoesntExistException {
        Reservation res =  users.get(email).getActiveReservation(id);
        if(res == null) throw new ReservationDoesntExistException();
        res.cancel();
    }


    /**
     * Calcula o valor em dívida atual.
     * Este valor é calculado a partir da soma dos valores de todas as reservas canceladas e dos valores
     * acumulados até ao momento das reservas atuais.
     *
     * @return Dívida total acumulada em cêntimos.
     */
    public int getTotalDue(String email) {
        return users.get(email).getTotalDue();
    }

    public List<Integer> popCanceledAutionRes (String user) {
        return users.get(user).popCanceledAuctionReservations();
    }
}
