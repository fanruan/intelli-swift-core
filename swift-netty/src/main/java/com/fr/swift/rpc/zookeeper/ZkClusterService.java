//package com.fr.swift.rpc.zookeeper;
//
//import com.fr.swift.ClusterService;
//import com.fr.swift.log.SwiftLogger;
//import com.fr.swift.log.SwiftLoggers;
//import com.fr.swift.node.SwiftClusterNode;
//import org.I0Itec.zkclient.IZkDataListener;
//import org.I0Itec.zkclient.ZkClient;
//import org.I0Itec.zkclient.exception.ZkNodeExistsException;
//
///**
// * This class created on 2018/6/7
// *
// * @author Lucifer
// * @description
// * @since Advanced FineBI 5.0
// */
//public class ZkClusterService implements ClusterService {
//
//    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(ZkClusterService.class);
//
//    private ZkClient zkClient;
//    private final String MASTER_NODE = "/swift/master";
//    private final String NODE_LIST = "/swift/nodes";
//    private static int SESSION_TIMEOUT = 5000;
//    private static int CONNECTION_TIMEOUT = 1000;
//
//    private SwiftClusterNode serverNode;
//    private SwiftClusterNode masterNode;
//    private IZkDataListener masterListener;
//
//    public ZkClusterService(String zkAddress, SwiftClusterNode serverNode, int sessionTimeout, int connectionTimeout) {
//        zkClient = new ZkClient(zkAddress, sessionTimeout, connectionTimeout);
//        this.serverNode = serverNode;
//
//        masterListener = new IZkDataListener() {
//            @Override
//            public void handleDataChange(String dataPath, Object data) throws Exception {
//                LOGGER.info("zookeeper path:" + dataPath + "'s data change!");
//            }
//
//            @Override
//            public void handleDataDeleted(String dataPath) throws Exception {
//                LOGGER.info("zookeeper path:" + dataPath + " was deleted!");
//                competeMaster();
//            }
//        };
//        zkClient.subscribeDataChanges(MASTER_NODE, masterListener);
//    }
//
//    public ZkClusterService(String zkAddress, SwiftClusterNode serverNode, int sessionTimeout) {
//        this(zkAddress, serverNode, sessionTimeout, CONNECTION_TIMEOUT);
//    }
//
//    public ZkClusterService(String zkAddress, SwiftClusterNode serverNode) {
//        this(zkAddress, serverNode, SESSION_TIMEOUT, CONNECTION_TIMEOUT);
//    }
//
//
//    @Override
//    public synchronized boolean competeMaster() {
//        LOGGER.info("begin to compete master!");
//        try {
//            zkClient.createEphemeral(MASTER_NODE, serverNode);
//            masterNode = serverNode;
//            LOGGER.info("compete master succeed!");
//        } catch (ZkNodeExistsException e) {
//            LOGGER.info("compete master failed! check again!");
//            SwiftClusterNode serverNode = zkClient.readData(MASTER_NODE);
//            if (serverNode == null) {
//                LOGGER.info("sync master failed! compete master again!");
//                competeMaster();
//            } else {
//                LOGGER.info("sync master succeed!");
//                masterNode = serverNode;
//            }
//        }
//        LOGGER.info("end to compete master!");
//        return true;
//    }
//
//    @Override
//    public synchronized boolean registerNode() {
//        try {
//            if (!zkClient.exists(NODE_LIST)) {
//                zkClient.createPersistent(NODE_LIST);
//            }
//            zkClient.createEphemeral(NODE_LIST + "/" + serverNode.getId());
//            LOGGER.info("register local node succeed!");
//        } catch (ZkNodeExistsException e) {
//            LOGGER.error("register local node failed!", e);
//        }
//        return true;
//    }
//}
