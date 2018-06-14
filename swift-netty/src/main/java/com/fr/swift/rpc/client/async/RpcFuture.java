package com.fr.swift.rpc.client.async;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.rpc.bean.RpcRequest;
import com.fr.swift.rpc.bean.RpcResponse;
import com.fr.swift.rpc.client.AsyncRpcCallback;
import com.fr.swift.rpc.client.RpcClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class created on 2018/6/11
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class RpcFuture implements Future<Object> {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(RpcFuture.class);

    private Sync sync;
    private RpcRequest request;
    private RpcResponse response;
    private long startTime;
    private long responseTimeThreshold = 5000;

    private List<AsyncRpcCallback> pendingCallbacks = new ArrayList<AsyncRpcCallback>();
    private ReentrantLock lock = new ReentrantLock();

    public RpcFuture(RpcRequest request) {
        this.sync = new Sync();
        this.request = request;
        this.startTime = System.currentTimeMillis();
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

    @Override
    public Object get() {
        sync.acquire(-1);
        if (this.response != null) {
            return this.response.getResult();
        }
        return null;
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException {
        boolean success = sync.tryAcquireNanos(-1, unit.toNanos(timeout));
        if (success) {
            if (this.response != null) {
                return this.response.getResult();
            } else {
                return null;
            }
        } else {
            throw new RuntimeException("Timeout exception. Request id: " + this.request.getRequestId()
                    + ". Request class name: " + this.request.getInterfaceName()
                    + ". Request method: " + this.request.getMethodName());
        }
    }

    public void done(RpcResponse reponse) {
        this.response = reponse;
        sync.release(1);
        invokeCallbacks();
        long responseTime = System.currentTimeMillis() - startTime;
        LOGGER.warn("Async request done! Request id = " + reponse.getRequestId() + ". Response Time = " + responseTime + "ms");
    }

    private void invokeCallbacks() {
        lock.lock();
        try {
            for (final AsyncRpcCallback callback : pendingCallbacks) {
                runCallback(callback);
            }
        } finally {
            lock.unlock();
        }
    }

    public RpcFuture addCallback(AsyncRpcCallback callback) {
        lock.lock();
        try {
            if (isDone()) {
                runCallback(callback);
            } else {
                this.pendingCallbacks.add(callback);
            }
        } finally {
            lock.unlock();
        }
        return this;
    }

    private void runCallback(final AsyncRpcCallback callback) {
        final RpcResponse res = this.response;
        RpcClient.submit(new Runnable() {
            @Override
            public void run() {
                if (!res.isError()) {
                    callback.success(res.getResult());
                } else {
                    callback.fail(new RuntimeException("Response error", res.getException()));
                }
            }
        });
    }

    static class Sync extends AbstractQueuedSynchronizer {

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
}
