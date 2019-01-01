public interface ManagerInterface {

    void  registerUser(String email, String password) throws EmailAlreadyUsedException;

    boolean checkCredentials(String email, String password) throws EmailNaoExisteException;

    int createStandardReservation(String email, String serverType) throws Exception;

    int createAuctionReservation(String email, String serverType, int bid) throws EmailNaoExisteException, Exception;

    void cancelReservation(String email, int id) throws Exception;

    int getTotalDue(String email) throws EmailNaoExisteException;
}
