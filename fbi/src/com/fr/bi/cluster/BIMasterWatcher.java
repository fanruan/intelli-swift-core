package com.fr.bi.cluster;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.cluster.manager.ZooKeeperClusterHostManager;
import com.fr.bi.cluster.utils.IPAddressUtils;
import com.fr.bi.cluster.wrapper.ZooKeeperWrapper;
import com.fr.bi.cluster.zookeeper.BIWatcher;
import com.fr.bi.module.BICoreModule;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.data.Stat;

/**
 * Created by Lucifer on 2017-3-3.
 *
 * @author Lucifer
 * @since 4.0
 * 监视master节点的变化。
 */
public class BIMasterWatcher extends BIWatcher {

    private static String MASTER_PATH = "/cluster/master";

    private long LOAD_SLEEP_TIME = 10000L;

    public BIMasterWatcher() {
    }

    @Override
    public void init(ZooKeeperWrapper zk) throws Exception {
        this.zk = zk;

        Stat s = zk.exists(MASTER_PATH, this);
        if (s == null) {
            ensureNodePathExists();
        } else {
            updateMasterInfo(false);
        }
        watch();
    }

    @Override
    public String getFocusedEventPath() {
        return MASTER_PATH;
    }

    @Override
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
                        try {
                            String rpcPort = ClusterManager.getInstance().getHostManager().getLocalRpcPort();

                            String masterInfo = IPAddressUtils.getAddress().getHostAddress() + ":" + rpcPort;
                            ensurePathExistsTemp(childPath, masterInfo);
                            updateMasterInfo(true);
                        } catch (KeeperException ke) {
                            BILoggerFactory.getLogger(BIMasterWatcher.class).info("Campaign for master failed! Stay slaver!");
                            updateMasterInfo(false);
                            return;
                        }
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
            //检测到节点消失，处罚竞选master事件。若是节点创建事件，则更新master信息
            if (event.getType().equals(Event.EventType.NodeDeleted)) {
                BILoggerFactory.getLogger(BIMasterWatcher.class).info("Master is shut down ! Campaign for new master!");
                if (zk.exists(MASTER_PATH, false) == null) {
                    ensureNodePathExists();
                } else {
                    updateMasterInfo(false);
                }
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger(BIMasterWatcher.class).error(e.getMessage(), e);
        }
    }

    private void updateMasterInfo(boolean isMaster) throws Exception {
        //更新master ip、self等信息。
        ZooKeeperClusterHostManager newHostManager = new ZooKeeperClusterHostManager();
        ClusterHostManagerInterface oldHostManager = ClusterManager.getInstance().getHostManager();

        try {
            String[] datas = new String(zk.getData(MASTER_PATH, false, new Stat())).split(":");
            newHostManager.setIp(datas[0]);
            newHostManager.setPort(Integer.valueOf(datas[1]));
        } catch (Exception e) {
            BILoggerFactory.getLogger(BIMasterWatcher.class).error(e.getMessage(), e);
            newHostManager.setIp(oldHostManager.getIp());
            newHostManager.setPort(oldHostManager.getPort());
        }
        newHostManager.setLocalRpcPort(oldHostManager.getLocalRpcPort());
        newHostManager.setSelf(isMaster);
        ClusterManager.getInstance().setZooKeeperClusterHostManager(newHostManager);

        //切换hostManager后，根据是否master对BICoreModule重载。
        if (ClusterManager.getInstance().getHostManager().isSelf()) {
            BILoggerFactory.getLogger(BIMasterWatcher.class).info("----------------switch to master-------------------");
        } else {
            BILoggerFactory.getLogger(BIMasterWatcher.class).info("----------------switch to slaver-------------------");
            Thread.sleep(LOAD_SLEEP_TIME);
        }
        BICoreModule biCoreModule = new BICoreModule();
        biCoreModule.start();
    }

}
