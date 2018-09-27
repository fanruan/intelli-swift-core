package com.fr.swift.netty.rpc.client;

import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;

import java.util.concurrent.ExecutorService;

/**
 * This class created on 2018/6/6
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class RpcClient {

    private static ExecutorService executorService = SwiftExecutors.newFixedThreadPool(16, new PoolThreadFactory(RpcClient.class));

    public static void submit(Runnable task) {
        executorService.submit(task);
    }

    public static void stop() {
        executorService.shutdown();
//        RpcClientConnector.getInstance().stop();
    }
}
