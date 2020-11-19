package com.fr.swift.executor.task.netty.service;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.executor.task.netty.server.MigNettyServerStarter;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.netty.rpc.server.NettyServiceStarter;
import com.fr.swift.service.ServerService;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;

/**
 * @author Heng.J
 * @date 2020/11/17
 * @description
 * @since swift-1.2.0
 */
@SwiftBean()
@com.fr.swift.annotation.ServerService(name = "mig")
public class SwiftMigNettyService implements ServerService {

    public static final SwiftMigNettyService INSTANCE = new SwiftMigNettyService();
    private NettyServiceStarter serverStarter;
    private ExecutorService tcpServerExecutor = SwiftExecutors.newSingleThreadExecutor(new PoolThreadFactory("netty-mig-server"));

    private SwiftMigNettyService() {
    }

    public static SwiftMigNettyService getInstance() {
        return INSTANCE;
    }

    @Override
    public void startServerService() {
        if (serverStarter == null || tcpServerExecutor.isShutdown()) {
            synchronized (this.getClass()) {
                if (serverStarter == null) {
                    serverStarter = new MigNettyServerStarter();
                }
                if (tcpServerExecutor.isShutdown()) {
                    tcpServerExecutor = SwiftExecutors.newSingleThreadExecutor(new PoolThreadFactory("netty-mig-server"));
                }
            }
        }
        tcpServerExecutor.submit(() -> {
            try {
                SwiftLoggers.getLogger().info("mig server starting!");
                serverStarter.start();
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
            }
        });
    }

    @PreDestroy
    @Override
    public void stopServerService() throws Exception {
        synchronized (this.getClass()) {
            SwiftLoggers.getLogger().info("mig server stopping!");
            if (serverStarter != null) {
                serverStarter.stop();
            }
            tcpServerExecutor.shutdownNow();
        }
    }
}
