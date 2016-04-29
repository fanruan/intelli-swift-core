package com.fr.bi.cluster.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;

/**
 * Created by Connery on 2015/3/26.
 */
public class BIDefaultWatcher extends BIWatcher {
    @Override
    public String getFocusedEventPath() {
        return "";
    }

    @Override
    public void eventProcessor(WatchedEvent event) {

    }

    @Override
    public void ensureNodePathExists() throws KeeperException, InterruptedException {

    }

    @Override
    public void watch() {
    }


}