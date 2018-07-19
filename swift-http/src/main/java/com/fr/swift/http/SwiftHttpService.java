package com.fr.swift.http;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.netty.NettyServiceStarter;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;
import com.fr.third.springframework.context.ApplicationContext;

import java.util.concurrent.ExecutorService;

/**
 * This class created on 2018/7/18
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftHttpService {

    private NettyServiceStarter serverStarter;

    private ApplicationContext context;

    private ExecutorService httpServerExecutor = SwiftExecutors.newSingleThreadExecutor(new PoolThreadFactory("netty-http-server"));

    private SwiftHttpService() {
        init();
    }

    public static final SwiftHttpService INSTANCE = new SwiftHttpService();

    public static SwiftHttpService getInstance() {
        return INSTANCE;
    }

    private void init() {
        context = SwiftContext.getInstance();
    }

    public void startServerService() {

        synchronized (this.getClass()) {
            if (serverStarter == null) {
                serverStarter = new SwiftHttpServer();
            }
        }
        httpServerExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    SwiftLoggers.getLogger().info("http server starting!");
                    serverStarter.start();
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error(e);
                }
            }
        });
    }

    public synchronized void stopServerService() throws Exception {
        SwiftLoggers.getLogger().info("http server stopping!");
        if (serverStarter != null) {
            serverStarter.stop();
        }
        httpServerExecutor.shutdownNow();
    }

}
