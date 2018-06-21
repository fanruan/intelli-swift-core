package com.fr.swift.rpc.client;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.thread.SwiftExecutors;

import java.util.concurrent.ExecutorService;

/**
 * This class created on 2018/6/6
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class RpcClient {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(RpcClient.class);

    private static ExecutorService executorService = SwiftExecutors.newFixedThreadPool(16);

    public static void submit(Runnable task) {
        executorService.submit(task);
    }

    public static void stop() {
        executorService.shutdown();
//        RpcClientConnector.getInstance().stop();
    }
}
