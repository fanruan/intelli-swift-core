package com.fr.swift.cloud.util.concurrent;

import com.fr.swift.cloud.log.SwiftLoggers;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author anner
 * @date 19-7-30
 **/
public class ExceptionSafeScheduledExecutor implements ScheduledExecutorService {
    private ScheduledExecutorService scheduler;

    ExceptionSafeScheduledExecutor(ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
    }

    private static class Run implements Runnable {
        Runnable runnable;

        private Run(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() {
            try {
                runnable.run();
            } catch (Throwable e) {
                SwiftLoggers.getLogger().error(e);
            }
        }
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return scheduler.schedule(command, delay, unit);
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return scheduler.schedule(callable, delay, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return scheduler.scheduleAtFixedRate(new Run(command), initialDelay, period, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return scheduler.scheduleWithFixedDelay(new Run(command), initialDelay, delay, unit);
    }

    @Override
    public void shutdown() {
        scheduler.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return scheduler.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return scheduler.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return scheduler.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return scheduler.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return scheduler.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return scheduler.submit(task, result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return scheduler.submit(task);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return scheduler.invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return scheduler.invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return scheduler.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return scheduler.invokeAny(tasks, timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        scheduler.execute(command);
    }
}
