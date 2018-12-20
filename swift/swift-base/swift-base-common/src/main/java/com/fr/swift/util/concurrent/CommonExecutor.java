package com.fr.swift.util.concurrent;

import java.util.concurrent.ExecutorService;

/**
 * @author anchore
 * @date 2018/6/14
 */
public class CommonExecutor {

    private ExecutorService exec = SwiftExecutors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new PoolThreadFactory(getClass()));

    public static ExecutorService get() {
        return INSTANCE.exec;
    }

    private static final CommonExecutor INSTANCE = new CommonExecutor();

    private CommonExecutor() {
    }
}