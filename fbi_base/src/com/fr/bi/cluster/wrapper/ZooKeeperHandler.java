package com.fr.bi.cluster.wrapper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * Created by FineSoft on 2015/5/7.
 */
public class ZooKeeperHandler {
    private ZooKeeper zkHandler;
    private ZooKeeperConfig config;
    private Watcher watcher;

    public synchronized ZooKeeper getZooKeeper() {
        if (zkHandler.getState() == ZooKeeper.States.CLOSED) {
            reconnect();
        }else {
            waitUntilConnected(zkHandler);
        }
        return zkHandler;
    }

    public ZooKeeperHandler(ZooKeeperConfig config, Watcher watcher) {
        connect(config, watcher);

    }

    public void reconnect() {
        connect(config, watcher);
    }

    private void connect(ZooKeeperConfig config, Watcher watcher) {
        try {
            zkHandler = new ZooKeeper(config.getConnectString(), config.getTickTime(), watcher);
            this.config = config;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void waitUntilConnected(ZooKeeper zooKeeper) {
        CountDownLatch connectedLatch = new CountDownLatch(1);
        watcher = new ConnectedWatcher(connectedLatch);
        zooKeeper.register(watcher);
        if (ZooKeeper.States.CONNECTING == zooKeeper.getState()) {
            try {
                connectedLatch.await();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    static class ConnectedWatcher implements Watcher {

        private CountDownLatch connectedLatch;

        ConnectedWatcher(CountDownLatch connectedLatch) {
            this.connectedLatch = connectedLatch;
        }

        @Override
        public void process(WatchedEvent event) {
            if (event.getState() == Event.KeeperState.SyncConnected) {
                connectedLatch.countDown();
            }
            if (event.getState() == Event.KeeperState.Disconnected){

            }
        }
    }

}