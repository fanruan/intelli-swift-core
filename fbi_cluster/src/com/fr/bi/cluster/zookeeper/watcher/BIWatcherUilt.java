package com.fr.bi.cluster.zookeeper.watcher;

import com.fr.base.FRContext;
import com.fr.general.FRLogger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;


/**
 * Created by Connery on 2015/3/27.
 */
public class BIWatcherUilt {

    private static final FRLogger LOG = FRContext.getLogger();

    public static String findIDInChildren(String prefix, ZooKeeper zookeeper, String dir)
            throws KeeperException, InterruptedException {
        String id = "";
        List<String> names = zookeeper.getChildren(dir, false);
        for (String name : names) {
            if (name.startsWith(prefix)) {
                id = name;

                LOG.debug("Found id created last time: " + id);

                break;
            }
        }
        return id;
    }
}