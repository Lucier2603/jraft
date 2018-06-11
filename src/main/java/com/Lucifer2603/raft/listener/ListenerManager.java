package com.Lucifer2603.raft.listener;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * ListenerManager
 *
 * @author zhangchen20
 */
public class ListenerManager implements Listener {

    private ConcurrentLinkedQueue<Listener> listeners = new ConcurrentLinkedQueue<>();
    private ReadWriteLock rwLock;

    // TODO register cannot get write lock due to too much read locks.
    public void register(Listener listener) {
        Lock lock = rwLock.writeLock();

        try {
            lock.tryLock(30, TimeUnit.SECONDS);
            listeners.offer(listener);
            lock.unlock();
        } catch (InterruptedException inteEx) {
            throw new RuntimeException("");
        } catch (Exception e) {
            lock.unlock();
            throw new RuntimeException("");
        }
    }

    public void invoke() {
        Lock lock = rwLock.readLock();

        try {
            lock.tryLock(5, TimeUnit.SECONDS);

            for (Listener listener : listeners) {
                listener.invoke();
            }

            lock.unlock();
        } catch (InterruptedException inteEx) {
            throw new RuntimeException("");
        } catch (Exception e) {
            lock.unlock();
            throw new RuntimeException("");
        }
    }
}
