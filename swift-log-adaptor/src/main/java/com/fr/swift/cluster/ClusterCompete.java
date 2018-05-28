package com.fr.swift.cluster;

import com.fr.cluster.ClusterBridge;
import com.fr.cluster.core.ClusterNode;
import com.fr.cluster.engine.rpc.base.InvokerManager;
import com.fr.cluster.engine.rpc.proxy.FineRPCProxyFactory;
import com.fr.cluster.rpc.base.Invocation;
import com.fr.cluster.rpc.base.Invoker;
import com.fr.cluster.rpc.proxy.RPCInvokeStrategy;
import com.fr.cluster.rpc.proxy.RPCProxyFactory;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;

import java.util.concurrent.locks.Lock;

/**
 * This class created on 2018/5/10
 *
 * @author Lucifer
 * @description master竞争
 * @since Advanced FineBI 5.0
 */
public class ClusterCompete {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(ClusterCompete.class);
    private Lock clusterLock;

    private static final ClusterCompete INSTANCE = new ClusterCompete();

    private ClusterCompete() {
        this.clusterLock = ClusterBridge.getLockFactory().get("compete");
    }

    public static ClusterCompete getInstance() {
        return INSTANCE;
    }

    public synchronized boolean setUpMaster(String newMasterId) {
        ClusterNode newClusterNode = ClusterBridge.getView().getNodeById(newMasterId);
        LOGGER.info("Set up " + newMasterId + " master node");
        if (newClusterNode != null) {
            ClusterNodeManager.getInstance().setMasterNode(newClusterNode);
            LOGGER.info("Set up " + newMasterId + " master node successed!");
        } else {
            LOGGER.info("Set up " + newMasterId + " master node failed!");
        }
        return true;
    }

    public synchronized void competeMaster() {
        ClusterNode currentNode = ClusterNodeManager.getInstance().getCurrentNode();
        ClusterNode oldMasterNode = ClusterNodeManager.getInstance().getMasterNode();
        ClusterNode tempMasterNode = oldMasterNode != null ? ClusterBridge.getView().getNodeById(oldMasterNode.getID()) : null;

        //加集群锁
        //如果master不在集群中了，则竞争master
        if (tempMasterNode == null) {
            try {
                clusterLock.lock();
                //抢到锁，再次判断masternode，依然为空，则把自己置为master，并向集群中其他机器发送消息
                LOGGER.info("Check compete master!");
                oldMasterNode = ClusterNodeManager.getInstance().getMasterNode();
                tempMasterNode = oldMasterNode != null ? ClusterBridge.getView().getNodeById(oldMasterNode.getID()) : null;
                if (tempMasterNode == null) {
                    LOGGER.info("Begin to compete master!");
                    setUpMaster(currentNode.getID());
                    Invoker invoker = InvokerManager.getInstance().create(ClusterCompete.class);
                    Invocation invocation = Invocation.create(ClusterCompete.class, "setUpMaster", new Class[]{String.class}, currentNode.getID());
                    invoker.invokeAll(invocation);
                }
                LOGGER.info("End of compete master!");
            } finally {
                clusterLock.unlock();
            }
        }
    }

}
