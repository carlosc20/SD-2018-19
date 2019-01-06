import java.util.HashMap;
import java.util.Map;

public class ReadWriteMap<k,v> {
    private final Map<k,v> map;
    private final RWLock lock = new RWLock();

    public ReadWriteMap(Map<k,v> map)  {
        this.map = map;
    }

    public ReadWriteMap()  {
        this.map = new HashMap<>();
    }

    public v putIfAbsent(k key, v value) throws InterruptedException {
        lock.writeLock();
        try{
            return map.putIfAbsent(key,value);
        }finally {
            lock.writeUnlock();
        }
    }

    public v put (k key, v value) throws InterruptedException {
        lock.writeLock();
        try{
            return map.put(key, value);
        }finally {
            lock.writeUnlock();
        }
    }

    public boolean remove(k key, v value) throws InterruptedException {
        lock.writeLock();
        try{
            return map.remove(key,value);
        } finally {
            lock.writeUnlock();
        }
    }

    public v remove(Object key) throws InterruptedException {
        lock.writeLock();
        try {
            return map.remove(key);
        } finally {
            lock.writeUnlock();
        }
    }

    public v get(k key) throws InterruptedException {
        lock.readLock();
        try{
            return map.get(key);
        } finally {
            lock.readUnlock();
        }
    }
}
