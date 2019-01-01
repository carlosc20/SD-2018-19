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
    private ReentrantLock lock;
    private Condition full;
    private Condition fullinho;
    private int standardQueue;

    public ServerType(int price, int total) {
        this.price = price;
        this.total = total;
        auctionResSet = new TreeSet<>();
        queue = new PriorityQueue<>();
        lock = new ReentrantLock();
        full = lock.newCondition();
        fullinho  = lock.newCondition();
        standardQueue = 0;
    }


    public int getPrice() {
        return price;
    }



    /**
     *
     */
    public StandardReservation addStandardRes(User user) {
        try {
            lock.lock();

            StandardReservation res = new StandardReservation(this, user);

            if (standardRes == total) {
                standardQueue++;
                while (standardRes == total) {                      // cheio, vai para fila
                    try {
                        full.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //addToQueue(res);
                }
                standardQueue--;
            } else if (standardRes + auctionRes == total) {         // cheio mas tem reservas de leilao
                System.out.println("cheio mas com res de leilao");
                AuctionReservation low = auctionResSet.last();      // remove com menor licitacao
                low.cancel();
                auctionRes--;
            }                                                       // else tem servidores livres

            // adiciona
            //queue.remove(res);
            res.setStartTime(LocalDateTime.now());
            standardRes++;
            return res;
        } finally {
            lock.unlock();
        }
    }


    /**
     *
     */
    public AuctionReservation addAuctionRes(User user, int bid) {
        try {
            lock.lock();

            AuctionReservation res = new AuctionReservation(this, user, bid);

            if (standardRes == total) { // cheio, vai para fila
                waitForBest(res);
            } else if (standardRes + auctionRes == total) {  // cheio mas tem reservas de leilao
                if (standardRes + auctionRes == total) {
                    AuctionReservation low = auctionResSet.last();
                    if (low.getPrice() < res.getPrice()) { // se for melhor que a pior reserva de leilao, remove essa
                        low.cancel();
                        auctionRes--;
                    }
                } else {
                    waitForBest(res);
                }
            }                                                           // else tem servidores livres

            // adiciona
            queue.remove(res);
            res.setStartTime(LocalDateTime.now());
            auctionRes++;
            auctionResSet.add(res);

            return res;
        } finally {
            lock.unlock();
        }
    }

    private void waitForBest(AuctionReservation res) {
        do {
            try {
                fullinho.await();
                if (standardRes + auctionRes == total) {
                    AuctionReservation low = auctionResSet.last();
                    if (low.getPrice() < res.getPrice()) { // se for melhor que a pior reserva de leilao, remove essa
                        low.cancel();
                        auctionRes--;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (standardRes == total || standardQueue != 0);
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


    public void forceCancelRes(AuctionReservation res) {
        try {
            lock.lock();
            auctionResSet.remove(res);
            auctionRes--;
        } finally {
            lock.unlock();
        }
    }

    /**
     *  Remove a reserva da lista de reservas dos servidores libertando assim uma instância
     */
    public void cancelRes(Reservation res) {
        try {
            lock.lock();
            if (res instanceof AuctionReservation) {
                auctionResSet.remove(res);
                auctionRes--;
            } else {
                standardRes--;
                full.signal();
            }
            if(standardQueue == 0) fullinho.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
