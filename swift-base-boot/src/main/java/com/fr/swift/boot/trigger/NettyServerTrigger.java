package com.fr.swift.boot.trigger;

import com.fr.swift.executor.task.netty.server.FileUploadServer;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.trigger.SwiftPriorityInitTrigger;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;

import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Hoky
 * @date 2020/11/6
 */
public class NettyServerTrigger implements SwiftPriorityInitTrigger {

    private ScheduledExecutorService executorService = SwiftExecutors.newScheduledThreadPool(1, new PoolThreadFactory(getClass()));

    @Override
    public void init() {
        SwiftLoggers.getLogger().info("starting netty server ...");
        FileUploadServer fileUploadServer = new FileUploadServer();
        fileUploadServer.setPort(8124);
        fileUploadServer.bind();
    }

    @Override
    public void destroy() throws InterruptedException {
        SwiftLoggers.getLogger().info("stopping task migrate...");
    }

    @Override
    public int priority() {
        return SwiftPriorityInitTrigger.Priority.LOW.priority();
    }
}
