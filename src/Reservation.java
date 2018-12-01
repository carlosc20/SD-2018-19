import java.time.LocalDateTime;

public abstract class Reservation {

    private final String userEmail;
    private final Server server;
    private final LocalDateTime startTime;
    private LocalDateTime endTime; //pode ser null

    public Reservation(String userEmail, Server server, LocalDateTime startTime) {
        this.userEmail = userEmail;
        this.server = server;
        this.startTime = startTime;
    }
}

