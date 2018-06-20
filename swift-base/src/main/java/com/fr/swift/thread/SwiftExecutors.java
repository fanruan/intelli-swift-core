package com.fr.swift.thread;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.third.springframework.stereotype.Service;

import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class created on 2018/6/15
 *
 * @author Lucifer
 * @description swift thread executors
 * @since Advanced FineBI 5.0
 */
public class SwiftExecutors {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftExecutors.class);

    private final static AtomicInteger executorCount = new AtomicInteger(1);

    private final static AtomicInteger threadCount = new AtomicInteger(1);

    private final static Map<Integer, ExecutorService> executorServiceMap = new ConcurrentHashMap<Integer, ExecutorService>();

    private final static Map<Integer, Thread> threadMap = new ConcurrentHashMap<Integer, Thread>();

    public static ExecutorService newFixedThreadPool(int nThreads) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(nThreads);
        executorServiceMap.put(executorCount.getAndIncrement(), fixedThreadPool);
        return fixedThreadPool;
    }

    public static ExecutorService newFixedThreadPool(int nThreads, ThreadFactory threadFactory) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(nThreads, threadFactory);
        executorServiceMap.put(executorCount.getAndIncrement(), fixedThreadPool);
        return fixedThreadPool;
    }

    public static ExecutorService newSingleThreadExecutor() {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        executorServiceMap.put(executorCount.getAndIncrement(), singleThreadExecutor);
        return singleThreadExecutor;
    }

    public static ExecutorService newSingleThreadExecutor(ThreadFactory threadFactory) {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor(threadFactory);
        executorServiceMap.put(executorCount.getAndIncrement(), singleThreadExecutor);
        return singleThreadExecutor;
    }

    public static ExecutorService newCachedThreadPool() {
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        executorServiceMap.put(executorCount.getAndIncrement(), cachedThreadPool);
        return cachedThreadPool;
    }

    public static ExecutorService newCachedThreadPool(ThreadFactory threadFactory) {
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool(threadFactory);
        executorServiceMap.put(executorCount.getAndIncrement(), cachedThreadPool);
        return cachedThreadPool;
    }

    public static ScheduledExecutorService newSingleThreadScheduledExecutor() {
        ScheduledExecutorService singleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        executorServiceMap.put(executorCount.getAndIncrement(), singleThreadScheduledExecutor);
        return singleThreadScheduledExecutor;
    }

    public static ScheduledExecutorService newSingleThreadScheduledExecutor(ThreadFactory threadFactory) {
        ScheduledExecutorService singleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor(threadFactory);
        executorServiceMap.put(executorCount.getAndIncrement(), singleThreadScheduledExecutor);
        return singleThreadScheduledExecutor;
    }

    public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize) {
        ScheduledExecutorService scheduledThreadPool = new ScheduledThreadPoolExecutor(corePoolSize);
        executorServiceMap.put(executorCount.getAndIncrement(), scheduledThreadPool);
        return scheduledThreadPool;
    }

    public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize, ThreadFactory threadFactory) {
        ScheduledExecutorService scheduledThreadPool = new ScheduledThreadPoolExecutor(corePoolSize, threadFactory);
        executorServiceMap.put(executorCount.getAndIncrement(), scheduledThreadPool);
        return scheduledThreadPool;
    }

    public static Thread newThread(Runnable runnable, String name) {
        Thread thread = new Thread(runnable, name);
        threadMap.put(threadCount.getAndIncrement(), thread);
        return thread;
    }

    public static Thread newThread(ThreadGroup group, Runnable target, String name, long stackSize) {
        Thread thread = new Thread(group, target, name, stackSize);
        threadMap.put(threadCount.getAndIncrement(), thread);
        return thread;
    }

    public static void shutdownAll() {
        for (Map.Entry<Integer, ExecutorService> executorServiceEntry : executorServiceMap.entrySet()) {
            try {
                executorServiceEntry.getValue().shutdown();
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }

        for (Map.Entry<Integer, Thread> threadEntry : threadMap.entrySet()) {
            try {
                threadEntry.getValue().interrupt();
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
    }

    public static void shutdownAllNow() {
        for (Map.Entry<Integer, ExecutorService> executorServiceEntry : executorServiceMap.entrySet()) {
            try {
                executorServiceEntry.getValue().shutdownNow();
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        for (Map.Entry<Integer, Thread> threadEntry : threadMap.entrySet()) {
            try {
                threadEntry.getValue().interrupt();
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
    }

    public static ExecutorService unconfigurableExecutorService(ExecutorService executor) {
        return Executors.unconfigurableExecutorService(executor);
    }

    public static ScheduledExecutorService unconfigurableScheduledExecutorService(ScheduledExecutorService executor) {
        return Executors.unconfigurableScheduledExecutorService(executor);
    }

    public static ThreadFactory defaultThreadFactory() {
        return Executors.defaultThreadFactory();
    }

    public static ThreadFactory privilegedThreadFactory() {
        return Executors.privilegedThreadFactory();
    }

    public static <T> Callable<T> callable(Runnable task, T result) {
        return Executors.callable(task, result);
    }

    public static Callable<Object> callable(Runnable task) {
        return Executors.callable(task);
    }

    public static Callable<Object> callable(final PrivilegedAction<?> action) {
        return Executors.callable(action);
    }

    public static Callable<Object> callable(final PrivilegedExceptionAction<?> action) {
        return Executors.callable(action);
    }

    public static <T> Callable<T> privilegedCallable(Callable<T> callable) {
        return Executors.privilegedCallable(callable);
    }

    public static <T> Callable<T> privilegedCallableUsingCurrentClassLoader(Callable<T> callable) {
        return Executors.privilegedCallableUsingCurrentClassLoader(callable);
    }
}

