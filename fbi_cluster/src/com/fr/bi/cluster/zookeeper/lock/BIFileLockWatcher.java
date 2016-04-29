package com.fr.bi.cluster.zookeeper.lock;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

/**
 * Created by Connery on 2015/4/14.
 */
public class BIFileLockWatcher implements Watcher {
    private Object locker;

    public BIFileLockWatcher(Object locker) {
        this.locker = locker;
    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println(event.getType());
        synchronized (locker) {
//            int i = 1;
//            while (i < 100) {
//                System.out.print(i++);
//            }
            locker.notify();
        }
    }
}