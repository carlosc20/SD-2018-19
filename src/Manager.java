import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;

/**
 *  Facade da lógica
 */
public class Manager implements ManagerInterface {

    private static Manager ourInstance;

    static {
        try {
            ourInstance = new Manager();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private final ReadWriteMap<String, User> users;         // chave email
    private final Map<String, ServerType> servers; // chave id


    private Manager() throws InterruptedException {
        this.users = new ReadWriteMap<>(new HashMap<>());
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
    public void registerUser(String email, String password) throws EmailAlreadyUsedException, InterruptedException {
        User u = new User(email, password);
        users.putIfAbsent(email,u);
    }


    /**
     * Verifica credenciais de um utilizador.
     *
     * @param  email o email do utilizador.
     * @param  password a password do utilizador.
     * @return true se o utilizador existir e a password se verificar.
     */
    public boolean checkCredentials(String email, String password) throws InterruptedException {
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
    public int createStandardReservation(String email, String serverType) throws ServerTypeDoesntExistException, InterruptedException {

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
    public int createAuctionReservation(String email, String serverType, int bid) throws ServerTypeDoesntExistException, InterruptedException {

        if(bid <= 0) throw new IllegalArgumentException(); // TODO: 01/01/2019 exception?

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
    public void cancelReservation(String email, int id) throws ReservationDoesntExistException, InterruptedException {
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
    public int getTotalDue(String email) throws InterruptedException {
        return users.get(email).getTotalDue();
    }


    /**
     * Devolve uma lista com as reservas canceladas enquanto o utilizador estava desconectado do servidor.
     *
     * @param  email o email do utilizador.
     * @return lista com os ids das reservas canceladas.
     */
    public List<Integer> getCanceledWhileOff(String email) throws InterruptedException {
        return users.get(email).popCanceledAuctionReservations();
    }
}
