package com.fr.bi.cluster;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.cluster.manager.TextClusterHostManager;
import com.fr.bi.cluster.manager.ZooKeeperClusterHostManager;
import com.fr.bi.cluster.wrapper.ZooKeeperWrapper;
import com.fr.bi.cluster.zookeeper.BIWatcher;
import com.fr.bi.module.BICoreModule;
import com.fr.engine.compare.CompareUtils;
import com.fr.general.ComparatorUtils;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.data.Stat;

import java.net.InetAddress;

/**
 * Created by Lucifer on 2017-3-3.
 *
 * @author Lucifer
 * @since 4.0
 * 监视master节点的变化。
 */
public class BIMasterWatcher extends BIWatcher {

    private static String MASTER_PATH = "/cluster/master";

    private String LOCAL_IP_ADDRESS = "127.0.0.1";

    public BIMasterWatcher() {
    }

    @Override
    public void init(ZooKeeperWrapper zk) throws Exception {
        this.zk = zk;

        Stat s = zk.exists(MASTER_PATH, this);
        if (s == null) {
            String serverIp = "";
            if (ClusterManager.getInstance().getHostManager() instanceof TextClusterHostManager) {
                //如果是配置，则根据配置，获取master
                if (((TextClusterHostManager) ClusterManager.getInstance().getHostManager()).isConfig()) {
                    serverIp = ClusterManager.getInstance().getHostManager().getIp();
                    if (ComparatorUtils.equals(serverIp, LOCAL_IP_ADDRESS)) {
                        serverIp = "";
                    }
                }
            }
            ensureNodePathExists(serverIp);
        }
        watch();
    }

    @Override
    public String getFocusedEventPath() {
        return MASTER_PATH;
    }


    public void ensureNodePathExists(String serverIp) throws Exception {
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
                        ensurePathExistsTemp(childPath, serverIp);
                    } else {
                        ensurePathExists(childPath);
                    }
                    childPath += "/";
                }
            }
        }
    }

    @Override
    public void eventProcessor(WatchedEvent event) {
        try {
            if (event.getType().equals(Event.EventType.NodeDeleted)) {
                System.out.print("Master is shut down ! Vote for new master!");
                if (zk.exists(MASTER_PATH, false) == null) {
                    ensureNodePathExists("");
                }
            } else if (event.getType().equals(Event.EventType.NodeCreated)
                    || event.getType().equals(Event.EventType.NodeDataChanged)) {
                //更新master ip、self等信息。
                ZooKeeperClusterHostManager newHostManager = new ZooKeeperClusterHostManager();
                ClusterHostManagerInterface oldHostManager = ClusterManager.getInstance().getHostManager();

                try {
                    String data = new String(zk.getData(MASTER_PATH, false, new Stat()));
                    String serverIp = InetAddress.getLocalHost().getHostAddress();
                    newHostManager.setIp(data);
                    if (CompareUtils.isEqual(data, serverIp)) {
                        newHostManager.setSelf(true);
                    } else {
                        newHostManager.setSelf(false);
                    }
                } catch (Exception e) {
                    BILoggerFactory.getLogger(BIMasterWatcher.class).error(e.getMessage(), e);
                    newHostManager.setIp(oldHostManager.getIp());
                    newHostManager.setSelf(oldHostManager.isSelf());
                }

                ClusterManager.getInstance().setHostManager(newHostManager);
                //切换hostManager后，根据是否master对BICoreModule重载。
                if (ClusterManager.getInstance().getHostManager().isSelf()) {
                    BILoggerFactory.getLogger(BIMasterWatcher.class).info("-------------------------switch to master-------------------------");
                } else {
                    BILoggerFactory.getLogger(BIMasterWatcher.class).info("-------------------------switch to slaver-------------------------");
                }
                BICoreModule biCoreModule = new BICoreModule();
                biCoreModule.start();
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger(BIMasterWatcher.class).error(e.getMessage(), e);
        }
    }


}
