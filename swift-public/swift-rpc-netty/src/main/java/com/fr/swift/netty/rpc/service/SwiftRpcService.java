package com.fr.swift.netty.rpc.service;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.netty.rpc.server.NettyServiceStarter;
import com.fr.swift.netty.rpc.server.RpcServerServiceStarter;
import com.fr.swift.service.ServerService;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;

/**
 * This class created on 2018/6/8
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@SwiftBean()
@com.fr.swift.annotation.ServerService(name = "rpc")
public class SwiftRpcService implements ServerService {

    public static final SwiftRpcService INSTANCE = new SwiftRpcService();
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftRpcService.class);
    private NettyServiceStarter serverStarter;
    private ExecutorService rpcServerExecutor = SwiftExecutors.newSingleThreadExecutor(new PoolThreadFactory("netty-rpc-server"));

    private SwiftRpcService() {
    }

    public static SwiftRpcService getInstance() {
        return INSTANCE;
    }

    @Override
    public void startServerService() {
        synchronized (this.getClass()) {
            if (serverStarter == null) {
                serverStarter = new RpcServerServiceStarter();
            }
        }
        if (rpcServerExecutor.isShutdown()) {
            rpcServerExecutor = SwiftExecutors.newSingleThreadExecutor(new PoolThreadFactory("netty-rpc-server"));
        }
        rpcServerExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    SwiftLoggers.getLogger().info("rpc server starting!");
                    serverStarter.start();
                } catch (Exception e) {
                    LOGGER.error(e);
                }
            }
        });
    }

    @PreDestroy
    @Override
    public synchronized void stopServerService() throws Exception {
        SwiftLoggers.getLogger().info("rpc server stopping!");
        if (serverStarter != null) {
            serverStarter.stop();
        }
        rpcServerExecutor.shutdownNow();
    }
}
