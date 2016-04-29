package com.fr.bi.cluster.zookeeper;

import org.apache.zookeeper.WatchedEvent;

/**
 * Created by Connery on 2015/3/26.
 */
public class BIReadDBWatcher extends BIWatcher {
    @Override
    public String getFocusedEventPath() {
        return null;
    }

    @Override
    public void eventProcessor(WatchedEvent event) {

    }
}