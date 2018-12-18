import java.util.HashMap;
import java.util.Map;

/**
 *  Facade da lógica
 */
public class Manager {

    private static Manager ourInstance = new Manager();

    private Map<String, User> users;         // chave email
    private Map<String, ServerType> servers; // chave id


    public Manager() {
        this.users = new HashMap<>();
        this.servers = new HashMap<>();
        // Exemplo:
        servers.put("t3.micro", new ServerType(300, 5));
        servers.put("t3.large", new ServerType(600, 3));
        servers.put("m5.micro", new ServerType(500, 1));
    }

    public static Manager getInstance() {
        return ourInstance;
    }


    /**
     * Regista um utilizador.
     *
     * @param email Email do novo utilizador
     * @param password Password do novo utilizador.
     */
    synchronized void registerUser(String email, String password) throws EmailJaExisteException {

        if (users.containsKey(email))
            throw new EmailJaExisteException(email);

        users.put(email, new User(password));
    }


    /**
     * Verifica credenciais de um utilizador.
     *
     * @return true se as credenciais se verificarem.
     */
    boolean checkCredentials(String email, String password) throws EmailNaoExisteException {

        User user = users.get(email);
        if (user == null) throw new EmailNaoExisteException(email);

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
    int createStandardReservation(String email, String serverType) {

        // TODO: acabar

        ServerType st = servers.get(serverType);
        StandardReservation res = st.addStandardRes();
        users.get(email).addReservation(res);

        return res.getId();
    }


    /**
     * Reserva servidor de um tipo em leilão.
     *
     * @return Id da reserva.
    */
    int createAuctionReservation(String email, String serverType, int bid) {

        // TODO: acabar

        ServerType st = servers.get(serverType);
        AuctionReservation res = st.addAuctionRes(bid);
        users.get(email).addReservation(res);

        return res.getId();
    }


    /**
     * Cancela uma reserva
     *
     * @param id Id da reserva.
     */
    void cancelReservation(String email, int id) throws Exception {
        users.get(email).cancelRes(id);
    }


    /**
     * Calcula o valor em dívida atual.
     * Este valor é calculado a partir da soma dos valores de todas as reservas canceladas e dos valores
     * acumulados até ao momento das reservas atuais.
     *
     * @return Dívida total acumulada em cêntimos.
     */
    int checkDebt(String email){

        // TODO: concorrencia
        return users.get(email).getTotalDue();
    }

}
