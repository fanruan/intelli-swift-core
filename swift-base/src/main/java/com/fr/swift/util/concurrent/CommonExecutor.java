package com.fr.swift.util.concurrent;

import com.fr.swift.thread.SwiftExecutors;

import java.util.concurrent.ExecutorService;

/**
 * @author anchore
 * @date 2018/6/14
 */
public class CommonExecutor {
    private static final ExecutorService EXECUTOR = SwiftExecutors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new PoolThreadFactory(CommonExecutor.class));

    public static ExecutorService get() {
        return EXECUTOR;
    }
}