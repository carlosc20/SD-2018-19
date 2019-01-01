import java.time.LocalDateTime;
import java.util.PriorityQueue;
import java.util.TreeSet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 *  Representa um conjunto de servidores do mesmo tipo
 */
public class ServerType {

    private final int price;    // Preço fixo por hora
    private final int total;    // Número fixo máximo de instâncias disponíveis

    private TreeSet<AuctionReservation> auctionResSet;

    private int standardRes;    // Número de instâncias ocupadas com reservas standard
    private int auctionRes;     // Número de instâncias ocupadas com reservas de leilão
    private PriorityQueue<Reservation> queue;   // Fila de espera de reservas


    public ServerType(int price, int total) {
        this.price = price;
        this.total = total;
        auctionResSet = new TreeSet<>();
        queue = new PriorityQueue<>();
    }


    public int getPrice() {
        return price;
    }
    /**
     *
     */
    public synchronized AuctionReservation addAuctionRes(User user, int bid) {

        AuctionReservation res = new AuctionReservation(this, user, bid);

        if(standardRes == total) {                                  // cheio, vai para fila
            addToQueue(res);
        } else if (standardRes + auctionRes == total) {             // cheio mas tem reservas de leilao
            AuctionReservation low = auctionResSet.last();
            if(low.getPrice() < res.getPrice()) {                   // se for melhor que a pior reserva de leilao, remove essa
                low.cancel();
                auctionRes--;
            } else {                                                // senao vai para fila
                addToQueue(res);
            }
        }                                                           // else tem servidores livres

        // adiciona
        queue.remove(res);
        res.setStartTime(LocalDateTime.now());
        auctionRes++;
        auctionResSet.add(res);

        return res;
    }


    /**
     *
     */
    public synchronized StandardReservation addStandardRes(User user) {
        System.out.println("inicio");
        StandardReservation res = new StandardReservation(this, user);

        if(standardRes == total) { // cheio, vai para fila
            System.out.println("cheio");
            addToQueue(res);
        } else if (standardRes + auctionRes == total) {         // cheio mas tem reservas de leilao
            System.out.println("cheio mas com res de leilao");
            AuctionReservation low = auctionResSet.last();      // remove com menor licitacao
            low.cancel();
            auctionRes--;
        }                                                       // else tem servidores livres

        // adiciona
        queue.remove(res);
        res.setStartTime(LocalDateTime.now());
        standardRes++;
        return res;
    }


    /**
     *  Adiciona uma reserva à fila de espera para ser atribuída a um servidor.
     */
    public void addToQueue(Reservation res) {
        queue.add(res);
        while (standardRes + auctionRes == total && queue.peek() == res) {
            try {
                wait();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }


    /**
     *  Remove a reserva da lista de reservas dos servidores libertando assim uma instância
     */
    public synchronized void cancelRes(Reservation res) {
        if(res instanceof AuctionReservation) {
            auctionResSet.remove(res);
            auctionRes--;
        } else {
            standardRes--;
        }
        notifyAll();
    }
}
