import java.util.List;
import java.util.Map;

public class User {

    private String email;
    private String password;
    private Map<Integer, Reservation> currentRes;
    private List<Reservation> canceledRes;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public List<Reservation> getCanceledRes() {
        return canceledRes;
    }

    public String getPassword() {
        return password;
    }

    public void cancelRes(int id) {
        canceledRes.add(currentRes.remove(id));
    }
}
