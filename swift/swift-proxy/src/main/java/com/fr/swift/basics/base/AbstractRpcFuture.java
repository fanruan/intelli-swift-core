package com.fr.swift.basics.base;

import com.fr.swift.basics.AsyncRpcCallback;
import com.fr.swift.basics.RpcFuture;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class created on 2018/8/14
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public abstract class AbstractRpcFuture<T> implements RpcFuture<T> {

    protected static final SwiftLogger LOGGER = SwiftLoggers.getLogger();
    private static ExecutorService executorService = SwiftExecutors.newFixedThreadPool(16, new PoolThreadFactory(AbstractRpcFuture.class));
    protected Sync sync;
    protected long startTime;

    protected List<AsyncRpcCallback> pendingCallbacks = new ArrayList<AsyncRpcCallback>();
    protected ReentrantLock lock = new ReentrantLock();

    public AbstractRpcFuture() {
        this.sync = new Sync();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDone() {
        return sync.isDone();
    }

    protected static class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 1L;

        private final int done = 1;
        private final int pending = 0;

        @Override
        protected boolean tryAcquire(int arg) {
            return getState() == done;
        }

        @Override
        protected boolean tryRelease(int arg) {
            if (getState() == pending) {
                return compareAndSetState(pending, done);
            } else {
                return true;
            }
        }

        public boolean isDone() {
            getState();
            return getState() == done;
        }
    }

    protected void submit(Runnable task) {
        executorService.submit(task);
    }

    protected void stop() {
        executorService.shutdown();
//        RpcClientConnector.getInstance().stop();
    }
}
