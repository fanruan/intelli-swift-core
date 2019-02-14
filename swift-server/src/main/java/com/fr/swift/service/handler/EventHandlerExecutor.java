package com.fr.swift.service.handler;

import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;

import java.util.concurrent.ExecutorService;

/**
 * This class created on 2018/12/29
 *
 * @author Lucifer
 * @description
 */
public class EventHandlerExecutor {

    private static ExecutorService executorService = SwiftExecutors.newSingleThreadExecutor(new PoolThreadFactory("EventHandlerExecutor"));

    public static void submit(Runnable task) {
        executorService.submit(task);
    }
}
