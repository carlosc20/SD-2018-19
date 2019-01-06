import java.util.List;

public class ManagerStub implements ManagerInterface {
    @Override
    public void registerUser(String email, String password) throws EmailAlreadyUsedException {

    }

    @Override
    public boolean checkCredentials(String email, String password) {
        return false;
    }

    @Override
    public int createStandardReservation(String email, String serverType) throws ServerTypeDoesntExistException {
        return 0;
    }

    @Override
    public int createAuctionReservation(String email, String serverType, int bid) throws ServerTypeDoesntExistException {
        return 0;
    }

    @Override
    public void cancelReservation(String email, int id) throws ReservationDoesntExistException {

    }

    @Override
    public int getTotalDue(String email) {
        return 0;
    }

    @Override
    public List<Integer> popCanceledWhileOff(String user) {
        return null;
    }

    @Override
    public void addCanceledWhileOff(String email, int resId) {

    }
}
