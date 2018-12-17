import java.time.LocalDateTime;

import static java.lang.Math.toIntExact;
import static java.time.temporal.ChronoUnit.HOURS;

public abstract class Reservation {

    private static int lastId = 0;
    private final int id;
    private final String userEmail;
    private final ServerType serverType;
    private final LocalDateTime startTime;
    private LocalDateTime endTime; // é null ao ser criado
    private int amountDue;


    public Reservation(String userEmail, ServerType serverType, LocalDateTime startTime) {
        this.id = lastId;
        lastId++;
        this.userEmail = userEmail;
        this.serverType = serverType;
        this.startTime = startTime;
    }

    public abstract int getPrice();


    public int getId() {
        return id;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public int getAmountDue() {
        return amountDue;
    }
    public int getCurrentAmountDue() { // TODO: confirmar
        long hours = HOURS.between(startTime, LocalDateTime.now());
        long price = this.getPrice();

        return toIntExact(hours * price);
    }

    public void cancel() {
        this.endTime = LocalDateTime.now();
        amountDue = getCurrentAmountDue(); // TODO: melhorar
    }
}

