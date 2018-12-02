import java.time.LocalDateTime;

public abstract class Reservation {

    private final String userEmail;
    private final ServerType serverType;
    private final LocalDateTime startTime;
    private LocalDateTime endTime; // é null ao ser criado

    public Reservation(String userEmail, ServerType serverType, LocalDateTime startTime) {
        this.userEmail = userEmail;
        this.serverType = serverType;
        this.startTime = startTime;
    }
}

