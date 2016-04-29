package com.fr.bi.cluster.zookeeper.lock;

import com.fr.bi.cluster.retry.RetryNTimes;
import com.fr.bi.cluster.wrapper.ZooKeeperConfig;
import com.fr.bi.cluster.wrapper.ZooKeeperHandler;
import com.fr.bi.cluster.wrapper.ZooKeeperWrapper;
import org.apache.zookeeper.ZooKeeper;

/**
 * Created by Connery on 2015/3/27.
 */
public class ZookeeperTestUtil {
    private static String ip = "127.0.0.1";

    public static ZooKeeperWrapper generateZookeeper(int tickTime) throws Exception {
        return getWrapper();
    }

    public static ZooKeeperWrapper getWrapper() {
        ZooKeeperConfig config = generateConfig();
        ZooKeeperHandler handler = new ZooKeeperHandler(config, null);
        ZooKeeperWrapper wrapper = new ZooKeeperWrapper(handler);

        wrapper.initial(new RetryNTimes(10, 3000));
        return wrapper;
    }

    private static ZooKeeperConfig generateConfig() {
        ZooKeeperConfig config = new ZooKeeperConfig();
        config.setConnectString(ip);
        config.setTickTime(100);
        return config;
    }

    public static ZooKeeper cloneZooKeeper(ZooKeeper zooKeeper) {
        ZooKeeperConfig config = generateConfig();
        try {
            return new ZooKeeper(config.getConnectString(), config.getTickTime(), null, zooKeeper.getSessionId(), zooKeeper.getSessionPasswd());

        } catch (Exception ex) {

        }
        return null;
    }
}