package com.fr.swift.service.register;

import com.fr.swift.cluster.manager.ClusterManager;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.netty.rpc.server.RpcServer;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.ClusterSwiftServerService;

/**
 * This class created on 2018/6/4
 *
 * @author Lucifer
 * @description todo 注册和注销service的动作均移动到master和slave的service里去做。service隔离通信层
 * @since Advanced FineBI 5.0
 */
public class ClusterSwiftRegister extends AbstractSwiftRegister {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(RpcServer.class);

    @Override
    public void serviceRegister() {
        if (ClusterSelector.getInstance().getFactory().isMaster()) {
            ClusterSwiftServerService.getInstance().start();
            masterLocalServiceRegister();
            SwiftContext.get().getBean("masterManager", ClusterManager.class).startUp();
        } else {
            remoteServiceRegister();
            SwiftContext.get().getBean("slaveManager", ClusterManager.class).startUp();
        }
    }

    @Override
    public void serviceUnregister() {
        try {
            SwiftContext.get().getBean("masterManager", ClusterManager.class).shutDown();
            SwiftContext.get().getBean("slaveManager", ClusterManager.class).shutDown();
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }
}
