package com.fr.swift.cloud.executor.task.netty.server;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.netty.rpc.server.NettyServiceStarter;

/**
 * @author Heng.J
 * @date 2020/11/17
 * @description
 * @since swift-1.2.0
 */
public class MigNettyServerStarter implements NettyServiceStarter {

    private MigrateServer migrateServer;

    @Override
    public void start() throws Exception {
        migrateServer = SwiftContext.get().getBean(MigrateServer.class);
        migrateServer.start();
    }

    @Override
    public void stop() throws Exception {

    }
}
