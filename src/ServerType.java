import java.time.LocalDateTime;
import java.util.PriorityQueue;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 *  Representa um conjunto de servidores do mesmo tipo
 */
public class ServerType {

    private final int price; // Preço fixo por hora
    private final int total; // Número fixo máximo de instâncias disponíveis

    private int standardActiveN; // Número de instâncias ocupadas com reservas standard
    private int auctionActiveN;  // Número de instâncias ocupadas com reservas de leilão
    private final SortedSet<AuctionReservation> auctionActiveSet; // Reservas de leilão ativas ordenadas por preço > id

    private int standardQueueN;
    private final PriorityQueue<AuctionReservation> auctionQueue; // Fila de espera de reservas

    private final ReentrantLock lock;
    private final Condition fullStandard;
    private final Condition fullAuction;


    /**
     * Construtor que recebe o preço por hora do tipo de servidor e o número de instâncias disponíveis para reserva.
     *
     * @param price o preco por hora de reserva standard.
     * @param total o número total de instâncias disponíveis para reserva.
     */
    public ServerType(int price, int total) {
        this.price = price;
        this.total = total;
        auctionActiveSet = new TreeSet<>();
        auctionQueue = new PriorityQueue<>();
        lock = new ReentrantLock();
        fullStandard = lock.newCondition();
        fullAuction = lock.newCondition();
        standardActiveN = 0;
        auctionActiveN = 0;
        standardQueueN = 0;
    }


    public int getPrice() {
        return price;
    }


    /**
     * Cria uma reserva standard quando possível que corresponde a um servidor deste tipo.
     * Usa os métodos
     *
     * @param  user o utilizador a que fica associada a reserva.
     * @return a reserva criada.
     */
    public StandardReservation addStandardRes(User user) {
        try {
            lock.lock();

            StandardReservation res = new StandardReservation(this, user);

            if (standardActiveN == total) {
                standardQueueN++;
                while (standardActiveN == total) {                      // cheio, vai para fila
                    try {
                        fullStandard.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                standardQueueN--;
            } else if (standardActiveN + auctionActiveN == total) {         // cheio mas tem reservas de leilao
                System.out.println("cheio mas com res de leilao");
                AuctionReservation low = auctionActiveSet.last();      // remove com menor licitacao
                low.cancel();
                auctionActiveN--;
            }                                                       // else tem servidores livres

            // adiciona
            res.setStartTime(LocalDateTime.now());
            standardActiveN++;
            return res;
        } finally {
            lock.unlock();
        }
    }


    /**
     * Cria uma reserva de leilão quando possível que corresponde a um servidor deste tipo.
     * Usa os métodos
     *
     * @param  user o utilizador a que fica associada a reserva.
     * @param  bid a licitação em cêntimos.
     * @return a reserva criada.
     */
    public AuctionReservation addAuctionRes(User user, int bid) {
        try {
            lock.lock();

            AuctionReservation res = new AuctionReservation(this, user, bid);

            if (standardActiveN == total) { // cheio, vai para fila
                waitForBest(res);
            } else if (standardActiveN + auctionActiveN == total) {  // cheio mas tem reservas de leilao
                AuctionReservation low = auctionActiveSet.last();
                if (low.getPrice() < res.getPrice()) { // se for melhor que a pior reserva de leilao, remove essa
                    low.forceCancel();
                } else {
                    waitForBest(res);
                }
            }                                                           // else tem servidores livres

            // adiciona
            res.setStartTime(LocalDateTime.now());
            auctionActiveN++;
            auctionActiveSet.add(res);
            return res;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Adiciona uma reserva á fila de espera.
     * Usado em {@link #addStandardRes(User)} e {@link #addAuctionRes(User, int)}.
     *
     * @param res reserva que é adicionada á fila de espera.
     */
    private void waitForBest(AuctionReservation res) {
        auctionQueue.add(res);
        do {
            try {
                fullAuction.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (standardActiveN + auctionActiveN == total || standardQueueN != 0 || auctionQueue.peek() != res);
        auctionQueue.remove(res);
    }


    /**
     * Remove a reserva da lista de reservas dos servidores libertando assim uma instância
     * Deve ser usado em vez do {@link #cancelRes(Reservation)} quando a reserva
     * for cancelada para ser substituída por outra.
     *
     * @param res a reserva que é cancelada.
     */
    public void forceCancelRes(AuctionReservation res) {
        try {
            lock.lock();
            auctionActiveSet.remove(res);
            auctionActiveN--;
        } finally {
            lock.unlock();
        }
    }


    /**
     * Remove a reserva da lista de reservas dos servidores libertando assim uma instância
     *
     * @param res reserva que é removida.
     */
    public void cancelRes(Reservation res) {
        try {
            lock.lock();
            if (res instanceof AuctionReservation) {
                auctionActiveSet.remove(res);
                auctionActiveN--;
            } else {
                standardActiveN--;
                fullStandard.signal();
            }
            if(standardQueueN == 0) fullAuction.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
