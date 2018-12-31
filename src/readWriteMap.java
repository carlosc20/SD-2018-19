import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class readWriteMap<k,v> {
    private final Map<k,v> map;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock r = lock.readLock();
    private final Lock w = lock.writeLock();

    public readWriteMap(Map<k,v> map){
        this.map = map;
    }
    public v put (k key, v value){
        w.lock();
        try{
            return map.put(key, value);
        }finally {
            w.unlock();
        }
    }
    public boolean remove(k key, v value) {
        w.lock();
        try{
            return map.remove(key,value);
        } finally {
            w.unlock();
        }
    }
    //podem ser adicionados mais métodos

    public v get(Object key){
        r.lock();
        try{
            return map.get(key);
        } finally {
            r.unlock();
        }
    }
}
