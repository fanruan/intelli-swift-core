package com.fr.swift.util.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author anchore
 * @date 2018/6/14
 */
public class CommonExecutor {
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new PoolThreadFactory(CommonExecutor.class));

    public static ExecutorService get() {
        return EXECUTOR;
    }
}