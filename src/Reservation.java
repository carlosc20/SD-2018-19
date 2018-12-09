import java.time.LocalDateTime;

public abstract class Reservation {

    private static int lastId = 0;
    private final int id;
    private final String userEmail;
    private final ServerType serverType;
    private LocalDateTime startTime;
    private LocalDateTime endTime; // é null ao ser criado
    private int amountDue;

    public int getAmountDue() {
        return amountDue;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public Reservation(String userEmail, ServerType serverType, LocalDateTime startTime) {
        this.id = lastId;
        lastId++;
        this.userEmail = userEmail;
        this.serverType = serverType;
        this.startTime = startTime;
    }

    public int getId() {
        return id;
    }
}

