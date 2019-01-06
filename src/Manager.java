import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * @param  email email do novo utilizador.
     * @param  password password do novo utilizador.
     * @throws EmailAlreadyUsedException se o email já existe.
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
     * @param  email o email do utilizador.
     * @param  password a password do utilizador.
     * @return true se o utilizador existir e a password se verificar.
     */
    public boolean checkCredentials(String email, String password) {
        User user = users.get(email);
        return (user != null && user.getPassword().equals(password));
    }


    /**
     * Cria reserva standard de um servidor de um tipo.
     *
     * @param  email o email do utilizador.
     * @param  serverType o tipo do servidor.
     * @return id da reserva criada.
     * @throws ServerTypeDoesntExistException se o tipo de servidor não existir.
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
     * Cria reserva de leilão de um servidor de um tipo, associada a uma licitação.
     *
     * @param  email o email do utilizador.
     * @param  serverType o tipo do servidor.
     * @param  bid a licitação em cêntimos.
     * @return id da reserva criada.
     * @throws ServerTypeDoesntExistException se o tipo de servidor não existir.
     */
    public int createAuctionReservation(String email, String serverType, int bid) throws ServerTypeDoesntExistException {

        if(bid <= 0) throw new IllegalArgumentException();

        ServerType st = servers.get(serverType);
        if(st == null) throw new ServerTypeDoesntExistException();

        User user = users.get(email);
        AuctionReservation res = st.addAuctionRes(user, bid); // espera até ser atribuída
        user.addReservation(res);

        return res.getId();
    }


    /**
     * Cancela uma reserva.
     *
     * @param  email o email do utilizador.
     * @param  id o id da reserva a cancelar.
     * @throws ReservationDoesntExistException se a reserva não existir ou já estiver cancelada.
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
     * @param email o email do utilizador.
     * @return Dívida total acumulada em cêntimos.
     */
    public int getTotalDue(String email) {
        return users.get(email).getTotalDue();
    }


    /**
     * Devolve uma lista com as reservas canceladas enquanto o utilizador estava desconectado do servidor.
     *
     * @param  email o email do utilizador.
     * @return lista com os ids das reservas canceladas.
     */
    public List<Integer> getCanceledWhileOff(String email) {
        return users.get(email).popCanceledAuctionReservations();
    }

    public void addCanceledWhileOff(String email, int resId){
        users.get(email).addCanceledAuctionReservation(resId);
    }
}
