package com.fr.swift.util.concurrent;

import com.fr.swift.log.SwiftLoggers;

import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class created on 2018/6/15
 *
 * @author Lucifer
 * @description swift thread executors
 * @since Advanced FineBI 5.0
 */
public class SwiftExecutors {

    private final static AtomicInteger EXECUTOR_COUNT = new AtomicInteger(1);

    private final static AtomicInteger THREAD_COUNT = new AtomicInteger(1);

    private final static Map<Integer, ExecutorService> EXECUTORS = new ConcurrentHashMap<Integer, ExecutorService>();

    private final static Map<Integer, Thread> THREADS = new ConcurrentHashMap<Integer, Thread>();

    public static ExecutorService newFixedThreadPool(int nThreads) {
        return newFixedThreadPool(nThreads, new PoolThreadFactory(SwiftExecutors.class));
    }

    public static ExecutorService newFixedThreadPool(int nThreads, ThreadFactory threadFactory) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(nThreads, threadFactory);
        EXECUTORS.put(EXECUTOR_COUNT.getAndIncrement(), fixedThreadPool);
        return fixedThreadPool;
    }

    public static ExecutorService newSingleThreadExecutor() {
        return newSingleThreadExecutor(new PoolThreadFactory(SwiftExecutors.class));
    }

    public static ExecutorService newSingleThreadExecutor(ThreadFactory threadFactory) {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor(threadFactory);
        EXECUTORS.put(EXECUTOR_COUNT.getAndIncrement(), singleThreadExecutor);
        return singleThreadExecutor;
    }

    public static ExecutorService newCachedThreadPool() {
        return newCachedThreadPool(new PoolThreadFactory(SwiftExecutors.class));
    }

    public static ExecutorService newCachedThreadPool(ThreadFactory threadFactory) {
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool(threadFactory);
        EXECUTORS.put(EXECUTOR_COUNT.getAndIncrement(), cachedThreadPool);
        return cachedThreadPool;
    }

    public static ScheduledExecutorService newSingleThreadScheduledExecutor() {
        return newSingleThreadScheduledExecutor(new PoolThreadFactory(SwiftExecutors.class));
    }

    public static ScheduledExecutorService newSingleThreadScheduledExecutor(ThreadFactory threadFactory) {
        ScheduledExecutorService singleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor(threadFactory);
        EXECUTORS.put(EXECUTOR_COUNT.getAndIncrement(), singleThreadScheduledExecutor);
        return singleThreadScheduledExecutor;
    }

    public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize) {
        return newScheduledThreadPool(corePoolSize, new PoolThreadFactory(SwiftExecutors.class));
    }

    public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize, ThreadFactory threadFactory) {
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(corePoolSize, threadFactory);
        EXECUTORS.put(EXECUTOR_COUNT.getAndIncrement(), scheduledThreadPool);
        return scheduledThreadPool;
    }

    public static ExecutorService newThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
                                                        long keepAliveTime, TimeUnit unit,
                                                        BlockingQueue<Runnable> workQueue,
                                                        ThreadFactory threadFactory) {

        ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                keepAliveTime, unit,
                workQueue,
                threadFactory);

        EXECUTORS.put(EXECUTOR_COUNT.getAndIncrement(), executor);
        return executor;
    }

    public static Thread newThread(Runnable runnable) {
        return newThread(runnable, BaseThreadFactory.getSimpleName(runnable.getClass()));
    }

    public static Thread newThread(Runnable runnable, String name) {
        return newThread(null, runnable, name, 0);
    }

    public static Thread newThread(ThreadGroup group, Runnable target, String name, long stackSize) {
        Thread t = BaseThreadFactory.newThread(group, target, name, stackSize);
        THREADS.put(THREAD_COUNT.getAndIncrement(), t);
        return t;
    }

    public static void shutdownAll() {
        for (Map.Entry<Integer, ExecutorService> executorServiceEntry : EXECUTORS.entrySet()) {
            try {
                executorServiceEntry.getValue().shutdown();
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
            }
        }

        for (Map.Entry<Integer, Thread> threadEntry : THREADS.entrySet()) {
            try {
                threadEntry.getValue().interrupt();
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
            }
        }
    }

    public static void shutdownAllNow() {
        for (Map.Entry<Integer, ExecutorService> executorServiceEntry : EXECUTORS.entrySet()) {
            try {
                executorServiceEntry.getValue().shutdownNow();
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
            }
        }
        for (Map.Entry<Integer, Thread> threadEntry : THREADS.entrySet()) {
            try {
                threadEntry.getValue().interrupt();
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
            }
        }
    }

    public static ExecutorService unconfigurableExecutorService(ExecutorService executor) {
        return Executors.unconfigurableExecutorService(executor);
    }

    public static ScheduledExecutorService unconfigurableScheduledExecutorService(ScheduledExecutorService executor) {
        return Executors.unconfigurableScheduledExecutorService(executor);
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