import java.util.List;

public interface ManagerInterface {

    void  registerUser(String email, String password) throws EmailAlreadyUsedException, InterruptedException;

    boolean checkCredentials(String email, String password) throws InterruptedException;

    int createStandardReservation(String email, String serverType) throws ServerTypeDoesntExistException, InterruptedException;

    int createAuctionReservation(String email, String serverType, int bid) throws ServerTypeDoesntExistException, InterruptedException;

    void cancelReservation(String email, int id) throws ReservationDoesntExistException, InterruptedException;

    int getTotalDue(String email) throws InterruptedException;

    List<Integer> getCanceledWhileOff(String user) throws InterruptedException;
}
