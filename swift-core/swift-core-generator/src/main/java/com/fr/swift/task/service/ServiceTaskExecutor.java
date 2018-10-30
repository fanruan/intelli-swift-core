package com.fr.swift.task.service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * This class created on 2018/7/12
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface ServiceTaskExecutor {

    TaskFuture submit(ServiceCallable serviceCallable) throws InterruptedException;


    class TaskFuture<T> implements Future<T> {
        Future<T> future;
        Semaphore semaphore = new Semaphore(0);

        public void setFuture(Future<T> future) {
            this.future = future;
            semaphore.release();
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return null != future &&
                    future.cancel(mayInterruptIfRunning);
        }

        @Override
        public boolean isCancelled() {
            return null != future &&
                    future.isCancelled();
        }

        @Override
        public boolean isDone() {
            return null != future &&
                    future.isDone();
        }

        @Override
        public T get() throws InterruptedException, ExecutionException {
            semaphore.acquire();
            return future.get();
        }

        @Override
        public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            semaphore.acquire();
            return future.get(timeout, unit);
        }
    }
}
