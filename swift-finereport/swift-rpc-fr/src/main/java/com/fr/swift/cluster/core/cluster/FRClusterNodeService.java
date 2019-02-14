package com.fr.swift.cluster.core.cluster;

import com.fr.cluster.ClusterBridge;
import com.fr.cluster.core.ClusterNode;
import com.fr.cluster.core.ClusterNodeState;
import com.fr.cluster.lock.ClusterLock;
import com.fr.cluster.lock.ClusterLockFactory;
import com.fr.general.ComparatorUtils;
import com.fr.swift.ClusterNodeService;
import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;

import java.util.List;

/**
 * This class created on 2018/8/15
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@SwiftBean
public class FRClusterNodeService implements ClusterNodeService<ClusterNode> {

    private ClusterLockFactory factory = ClusterBridge.getLockFactory();
    private ClusterLock clusterLock = factory.get(FRClusterNodeService.class);

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger();

    private SwiftServiceInfoService serviceInfoService = SwiftContext.get().getBean(SwiftServiceInfoService.class);

    private SwiftProperty swiftProperty = SwiftProperty.getProperty();

    private FRClusterNodeService() {
        swiftProperty.setRpcAddress(FRClusterNodeManager.getInstance().getCurrentId());
    }

    @Override
    public boolean competeMaster() {
        return competeMaster(null);
    }

    /**
     * @param leavingNode 离开集群的主节点
     * @return
     */
    @Override
    public boolean competeMaster(ClusterNode leavingNode) {
        ClusterNode currentNode = FRClusterNodeManager.getInstance().getCurrentNode();
        ClusterNode masterNode = FRClusterNodeManager.getInstance().getMasterNode();
        try {
            clusterLock.lock();
            List<SwiftServiceInfoBean> masterServiceInfoBeanList = serviceInfoService.getServiceInfoByService(ClusterNodeService.SERVICE);
            if (masterServiceInfoBeanList.isEmpty()) {
                LOGGER.info(String.format("Master is null,%s start to compete master!", currentNode.getID()));
                SwiftServiceInfoBean swiftServiceInfoBean = new SwiftServiceInfoBean(ClusterNodeService.SERVICE,
                        currentNode.getID(), currentNode.getID());
                serviceInfoService.saveOrUpdate(swiftServiceInfoBean);
                masterNode = currentNode;
                LOGGER.info(String.format("%s compete master succeeded!", currentNode.getID()));
            } else {
                SwiftServiceInfoBean clusterMasterBean = masterServiceInfoBeanList.get(0);
                String masterId = clusterMasterBean.getClusterId();
                if (masterIsDead(masterId, leavingNode)) {
                    LOGGER.info(String.format("Master %s is dead,%s start to compete master!", masterId, currentNode.getID()));
                    masterNode = currentNode;
                    SwiftServiceInfoBean swiftServiceInfoBean = new SwiftServiceInfoBean(ClusterNodeService.SERVICE,
                            currentNode.getID(), currentNode.getID());
                    serviceInfoService.saveOrUpdate(swiftServiceInfoBean);
                    LOGGER.info(String.format("%s compete master succeeded!", currentNode.getID()));
                } else {
                    LOGGER.info(String.format("Master %s is alive, %s sync master node!", masterId, currentNode.getID()));
                    masterNode = ClusterBridge.getView().getNodeById(masterId);
                    LOGGER.info(String.format("%s sync master node succeeded!", currentNode.getID()));
                }
            }

            swiftProperty.setMasterAddress(masterNode.getID());
            FRClusterNodeManager.getInstance().setMasterNode(masterNode);

            LOGGER.info("End to compete master !");
            return true;
        } catch (Exception e) {
            LOGGER.error(currentNode.getID() + " compete master falied!", e);
        } finally {
            clusterLock.unlock();
        }
        return false;
    }

    /**
     * 主节点不存在集群 or 主节点状态不是member or 离开节点不为空且为当前主节点
     *
     * @param masterId
     * @param leavingNode 离开集群的主节点
     * @return
     */
    private boolean masterIsDead(String masterId, ClusterNode leavingNode) {
        return ClusterBridge.getView().getNodeById(masterId) == null ||
                ClusterBridge.getView().getNodeById(masterId).getState() != ClusterNodeState.Member ||
                (leavingNode != null && ComparatorUtils.equals(leavingNode.getID(), masterId));
    }
}
