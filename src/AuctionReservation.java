import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;

/**
 *  Representa uma reserva em leilão de um servidor, pode ser cancelada pelo utilizador que a criou ou para dar lugar a uma reserva com oferta melhor ou standard.
 */
public class AuctionReservation extends Reservation implements Comparable {

    private final int bid;

    public AuctionReservation(ServerType serverType, User user, int bid) {
        super(serverType, user);
        this.bid = bid;
    }

    @Override
    public int getPrice() {
        return bid;
    }


    /**
     *
     */
    @Override
    public int compareTo(Object o) {
        AuctionReservation that = (AuctionReservation) o;
        int p1 = this.getPrice();
        int p2 = that.getPrice();
        if(p1 == p2) return Integer.compare(this.getId(), that.getId()); // menor id
        return Integer.compare(p2, p1);                                  // maior preço
    }

    /**
     * Cancela a reserva guardando o tempo de cancelamento e o montante total a pagar.
     */
    public void forceCancel() {
        this.getServerType().forceCancelRes(this);
        User user = this.getUser();
        user.cancelRes(this);
        this.setAmountDue(getAmountDue(LocalDateTime.now()));
        int id = this.getId();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket s = MainServer.getSocket(user.getEmail());
                if(s == null) return; // O utilizador não está conectado
                try {
                    PrintWriter wr = new PrintWriter(s.getOutputStream());
                    wr.println("A reserva id=" + id + " foi cancelada");
                    wr.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
