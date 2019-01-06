import java.util.List;

public interface ManagerInterface {

    void  registerUser(String email, String password) throws EmailAlreadyUsedException;

    boolean checkCredentials(String email, String password);

    int createStandardReservation(String email, String serverType) throws ServerTypeDoesntExistException;

    int createAuctionReservation(String email, String serverType, int bid) throws  ServerTypeDoesntExistException;

    void cancelReservation(String email, int id) throws ReservationDoesntExistException;

    int getTotalDue(String email);

    List<Integer> popCanceledWhileOff(String user);

    void addCanceledWhileOff(String email, int resId);
}
