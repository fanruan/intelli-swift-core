package com.fr.swift.cluster.service;

import com.fr.swift.ClusterNodeService;
import com.fr.swift.SwiftContext;
import com.fr.swift.annotation.ClusterService;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.nm.SlaveManager;
import com.fr.swift.node.SwiftClusterNode;
import com.fr.swift.node.SwiftClusterNodeManager;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.property.ZkProperty;
import com.fr.swift.rm.MasterManager;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.zk.SwiftZkClient;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;

import java.util.List;

/**
 * This class created on 2018/6/7
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@ClusterService(initMethod = "init", destroyMethod = "destroy")
@SwiftBean
public class ZkClusterService implements ClusterNodeService {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger();

    private static final String PARENT = "/swift";
    private static final String MASTER_NODE = PARENT + "/master";
    private static final String NODE_LIST = PARENT + "/nodes";

    private SwiftClusterNode serverNode;
    private SwiftClusterNode masterNode;
    private SwiftZkClient swiftZkClient;

    private SwiftProperty swiftProperty = SwiftProperty.getProperty();

    private ZkProperty zkProperty = SwiftContext.get().getBean(ZkProperty.class);

    private MasterManager masterManager = SwiftContext.get().getBean(MasterManager.class);

    private SlaveManager slaveManager = SwiftContext.get().getBean(SlaveManager.class);

    public void init() {
        swiftZkClient = new SwiftZkClient(zkProperty.getZookeeperAddress(), zkProperty.getSessionTimeout(), zkProperty.getConnectionTimeout());
        serverNode = (SwiftClusterNode) ClusterSelector.getInstance().getFactory().getCurrentNode();
        if (!swiftZkClient.exists(PARENT)) {
            swiftZkClient.createPersistent(PARENT);
        }
        registerNode();
        competeMaster();

        IZkDataListener masterListener = new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                LOGGER.info("zookeeper path:" + dataPath + "'s data change!");
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                LOGGER.info("zookeeper path:" + dataPath + " was deleted!");
                competeMaster();
                //只有重新选举master时候，才需要重新部署manager
                if (ClusterSelector.getInstance().getFactory().isMaster()) {
                    slaveManager.shutDown();
                    masterManager.startUp();
                    ClusterSwiftServerService.getInstance().initService();
                }
            }
        };
        IZkChildListener nodeLListener = new IZkChildListener() {
            @Override
            public void handleChildChange(String nodePath, List<String> childPaths) throws Exception {
                LOGGER.info("zookeeper path:" + nodePath + " has child change!");
                if (!childPaths.contains(serverNode.getId())) {
                    registerNode();
                }
            }
        };
        swiftZkClient.subscribeDataChanges(MASTER_NODE, masterListener);
        swiftZkClient.subscribeChildChanges(NODE_LIST, nodeLListener);
    }

    public void destroy() {
        swiftZkClient.unsubscribeAll();
        swiftZkClient.close();
    }

    @Override
    public synchronized boolean competeMaster() {
        LOGGER.info("begin to compete master!");
        try {
            swiftZkClient.createEphemeral(MASTER_NODE, serverNode);
            masterNode = serverNode;
            LOGGER.info("compete master succeed!");
        } catch (ZkNodeExistsException e) {
            LOGGER.info("compete master failed! check again!");
            SwiftClusterNode serverNode = swiftZkClient.readData(MASTER_NODE);
            if (serverNode == null) {
                LOGGER.info("sync master failed! compete master again!");
                competeMaster();
            } else {
                LOGGER.info("sync master succeed!");
                masterNode = serverNode;
            }
        }
        LOGGER.info("end to compete master!");
        swiftProperty.setMasterAddress(masterNode.getId());
        SwiftClusterNodeManager.getInstance().setMasterNode(masterNode);
        return true;
    }

    @Override
    public boolean competeMaster(Object o) {
        return false;
    }

    public synchronized boolean registerNode() {
        try {
            if (!swiftZkClient.exists(NODE_LIST)) {
                swiftZkClient.createPersistent(NODE_LIST);
            }
            swiftZkClient.createEphemeral(NODE_LIST + "/" + serverNode.getId());
            LOGGER.info("register local node succeed!");
        } catch (ZkNodeExistsException e) {
            LOGGER.error("register local node failed!", e);
            return false;
        }
        return true;
    }
}
