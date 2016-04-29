package com.fr.bi.cluster.wrapper;

import com.fr.bi.stable.utils.code.BILogger;
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

    public ZooKeeperHandler(ZooKeeperConfig config, Watcher watcher) {
        connect(config, watcher);

    }

    public static void waitUntilConnected(ZooKeeper zooKeeper) {
        CountDownLatch connectedLatch = new CountDownLatch(1);
        Watcher watcher = new ConnectedWatcher(connectedLatch);
        zooKeeper.register(watcher);
        if (ZooKeeper.States.CONNECTING == zooKeeper.getState()) {
            try {
                connectedLatch.await();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public ZooKeeper getZooKeeper() {
        if (zkHandler.getState() == ZooKeeper.States.CLOSED) {
//            System.out.println("当前Zookeeper客户端状态为：" + zkHandler.getState() + "，正在重新连接");
            reconnect();
        }
        waitUntilConnected(zkHandler);
//        System.out.println("当前Zookeeper客户端状态为：" + zkHandler.getState());
        return zkHandler;
    }

    public void reconnect(ZooKeeperConfig config, Watcher watcher) {
        connect(config, watcher);
    }

    public void reconnect() {
//        System.out.println("正在重新连接");
        connect(config, watcher);
    }

    private void connect(ZooKeeperConfig config, Watcher watcher) {
        try {
            zkHandler = new ZooKeeper(config.getConnectString(), config.getTickTime(), watcher);
            this.config = config;
        } catch (Exception ex) {
             BILogger.getLogger().error(ex.getMessage(), ex);
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
        }
    }

}