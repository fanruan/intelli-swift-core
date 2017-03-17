package com.fr.bi.cluster.zookeeper.watcher;

import com.fr.bi.cluster.utils.IPAddressUtils;
import com.fr.bi.cluster.wrapper.ZooKeeperWrapper;
import com.fr.bi.cluster.zookeeper.BIWatcher;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.data.Stat;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Lucifer on 2017-3-10.
 *
 * @author Lucifer
 * @since 4.0
 */
public class BINodeWatcher extends BIWatcher {

    private static String NODE_PATH = "/cluster/node";

    static {
        String ip = IPAddressUtils.getAddress().getHostAddress();
        NODE_PATH = NODE_PATH + "/" + ip;
    }

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @Override
    public void init(ZooKeeperWrapper zk) throws Exception {
        this.zk = zk;

        Stat s = zk.exists(NODE_PATH, this);
        if (s == null) {
            ensureNodePathExists();
        }
        watch();
    }

    public void ensureNodePathExists() throws Exception {
        String path = getFocusedEventPath();
        if (path != null || !path.isEmpty()) {
            if (path.charAt(0) == '/') {
                path = path.substring(1, path.length());
                if (path.charAt(path.length() - 1) == '/') {
                    path = path.substring(0, path.length() - 1);
                }
                String[] foldNames = path.split("/");
                String childPath = "/";
                for (int i = 0; i < foldNames.length; i++) {
                    childPath += foldNames[i];
                    if (i == foldNames.length - 1) {
                        String masterInfo = "Node start time : " + String.valueOf(dateFormat.format(new Date().getTime()));
                        ensurePathExistsTemp(childPath, masterInfo);
                    } else {
                        ensurePathExists(childPath);
                    }
                    childPath += "/";
                }
            }
        }
    }

    @Override
    public String getFocusedEventPath() {
        return NODE_PATH;
    }

    @Override
    public void eventProcessor(WatchedEvent event) {
    }
}
