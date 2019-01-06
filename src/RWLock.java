import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class RWLock {
    public static int test = 0;

    private static int currentTicket = 0;

    private ReentrantLock read = new ReentrantLock();
    private Condition notReading = read.newCondition();
    private Condition canRead = read.newCondition();
    private int reading;
    private boolean readIsBlocked = false;
    private static int lastTicket = 0;

    private ReentrantLock write = new ReentrantLock();

    public void readLock() throws InterruptedException {
        try {
            int id = currentTicket++;
            read.lock();
            while (id > lastTicket && readIsBlocked){
                canRead.await();
            }
            lastTicket++;
            canRead.signal();
            reading++;
        } finally {
            read.unlock();
        }
    }

    public void readUnlock() {
        try {
            read.lock();
            reading--;
            if(reading == 0) {
                notReading.signal();
            }
        } finally {
            read.unlock();
        }
    }

    public void writeLock() throws InterruptedException {
        int id = currentTicket++;

        read.lock();
        while (id > lastTicket) {
            canRead.await();
        }
        lastTicket++;
        readIsBlocked = true;
        while (reading > 0) {
            notReading.await();
        }
        write.lock();
    }

    public void writeUnlock() {
        try {
            readIsBlocked = false;
            canRead.signal();
        } finally {
            read.unlock();
            write.unlock();
        }
    }
}
