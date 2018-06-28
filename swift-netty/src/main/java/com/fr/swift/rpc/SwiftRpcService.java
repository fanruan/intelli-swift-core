package com.fr.swift.rpc;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.rpc.server.RpcServerServiceStarter;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;
import com.fr.third.springframework.context.ApplicationContext;

import java.util.concurrent.ExecutorService;

/**
 * This class created on 2018/6/8
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftRpcService {

    private ApplicationContext context;
    private NettyServiceStarter serverStarter;

    private ExecutorService rpcServerExecutor = SwiftExecutors.newSingleThreadExecutor(new PoolThreadFactory("netty-rpc-server"));

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftRpcService.class);

    private SwiftRpcService() {
        init();
    }

    public static final SwiftRpcService INSTANCE = new SwiftRpcService();

    public static SwiftRpcService getInstance() {
        return INSTANCE;
    }

    private void init() {
        context = SwiftContext.getInstance();
    }

    public void startServerService() {
        synchronized (this.getClass()) {
            if (serverStarter == null) {
                serverStarter = new RpcServerServiceStarter(context);
            }
        }
        rpcServerExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    serverStarter.start();
                } catch (Exception e) {
                    LOGGER.error(e);
                }
            }
        });
    }

    public synchronized void stopServerService() throws Exception {
        if (serverStarter != null) {
            serverStarter.stop();
        }
        rpcServerExecutor.shutdownNow();
    }
}
