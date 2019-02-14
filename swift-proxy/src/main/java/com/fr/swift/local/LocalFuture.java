package com.fr.swift.local;

import com.fr.swift.basics.AsyncRpcCallback;
import com.fr.swift.basics.RpcFuture;
import com.fr.swift.basics.base.AbstractRpcFuture;

import java.util.concurrent.TimeUnit;

/**
 * This class created on 2018/9/5
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class LocalFuture extends AbstractRpcFuture<LocalResult> {
    private LocalResult result;

    protected LocalFuture() {
        super();
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public Object get() {
        sync.acquire(-1);
        if (this.result != null) {
            return this.result.get();
        }
        return null;
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException {
        boolean success = sync.tryAcquireNanos(-1, unit.toNanos(timeout));
        if (success) {
            if (this.result != null) {
                return this.result.get();
            } else {
                return null;
            }
        } else {
            throw new RuntimeException("Timeout exception");
        }
    }

    @Override
    public void done(LocalResult result) {
        this.result = result;
        sync.release(1);
        invokeCallbacks();
        long responseTime = System.currentTimeMillis() - startTime;
        LOGGER.debug("Async request done! . Response Time = " + responseTime + "ms");
    }

    @Override
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

    private void runCallback(final AsyncRpcCallback callback) {
        final LocalResult result = this.result;
        submit(new Runnable() {
            @Override
            public void run() {
                if (result.getException() == null) {
                    callback.success(result.get());
                } else {
                    callback.fail(new RuntimeException("Response error", result.getException()));
                }
            }
        });
    }
}
