package com.fr.swift.frrpc;

import com.fr.cluster.ClusterBridge;
import com.fr.cluster.core.ClusterNode;
import com.fr.cluster.lock.ClusterLock;
import com.fr.cluster.lock.ClusterLockFactory;
import com.fr.swift.ClusterService;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;

import java.util.List;

/**
 * This class created on 2018/5/29
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftClusterService implements ClusterService {

    public static final String SERVICE = "cluster_master_service";

    private ClusterLockFactory factory = ClusterBridge.getLockFactory();
    private ClusterLock clusterLock = factory.get(SwiftClusterService.class);

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftClusterService.class);
    private SwiftServiceInfoService serviceInfoService = SwiftContext.getInstance().getBean(SwiftServiceInfoService.class);

    private SwiftClusterService() {
    }

    private static final ClusterService INSTANCE = new SwiftClusterService();

    public static ClusterService getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean competeMaster() {
        LOGGER.info("Start to compete master !");
        ClusterNode currentNode = ClusterNodeManager.getInstance().getCurrentNode();
        try {
            clusterLock.lock();
            List<SwiftServiceInfoBean> masterServiceInfoBeanList = serviceInfoService.getServiceInfoByService(SwiftClusterService.SERVICE);
            if (!masterServiceInfoBeanList.isEmpty()) {
                SwiftServiceInfoBean clusterMasterBean = masterServiceInfoBeanList.get(0);
                String masterId = clusterMasterBean.getClusterId();
                if (ClusterBridge.getView().getNodeById(masterId) == null) {
                    LOGGER.info("Master " + masterId + " is dead, " + currentNode.getID() + " start to compete master!");
                    ClusterNodeManager.getInstance().setMasterNode(currentNode);
                    SwiftServiceInfoBean swiftServiceInfoBean = new SwiftServiceInfoBean(SwiftClusterService.SERVICE, currentNode.getID(), "");
                    serviceInfoService.saveOrUpdateServiceInfo(swiftServiceInfoBean);
                    LOGGER.info(currentNode.getID() + " compete master succeeded!");
                } else {
                    LOGGER.info("Master " + masterId + " is alive, " + currentNode.getID() + " sync master node!");
                    ClusterNodeManager.getInstance().setMasterNode(ClusterBridge.getView().getNodeById(masterId));
                    LOGGER.info(currentNode.getID() + " sync master node succeeded!");
                }
            } else {
                LOGGER.info("Master is null," + currentNode.getID() + " start to compete master!");
                SwiftServiceInfoBean swiftServiceInfoBean = new SwiftServiceInfoBean(SwiftClusterService.SERVICE, currentNode.getID(), "");
                serviceInfoService.saveOrUpdateServiceInfo(swiftServiceInfoBean);
                LOGGER.info(currentNode.getID() + " compete master succeeded!");
            }
            LOGGER.info("End to compete master !");
            return true;
        } catch (Exception e) {
            LOGGER.error(currentNode.getID() + " compete master falied!", e);
        } finally {
            clusterLock.unlock();
        }
        return false;
    }

    @Override
    public String rpcSend(String masterId, Object object) {
        LOGGER.info(masterId + ":" + object.toString());
        return "response:" + masterId + ":" + object.toString();
    }

    @Override
    public String rpcSend() {
        LOGGER.info("time:" + System.currentTimeMillis());
        return "response:" + System.currentTimeMillis();
    }
}
