package com.fr.swift.rpc.client;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class created on 2018/6/6
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class RpcClient {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(RpcClient.class);

    private static ExecutorService executorService = Executors.newFixedThreadPool(16);

    public static void submit(Runnable task) {
        executorService.submit(task);
    }

    public static void stop() {
        executorService.shutdown();
//        RpcClientConnector.getInstance().stop();
    }
}
