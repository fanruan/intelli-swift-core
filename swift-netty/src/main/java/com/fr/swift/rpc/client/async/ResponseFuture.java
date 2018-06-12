package com.fr.swift.rpc.client.async;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.rpc.bean.RpcResponse;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * This class created on 2018/6/12
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ResponseFuture {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(ResponseFuture.class);

    public static ThreadLocal<Future<RpcResponse>> futureThreadLocal = new ThreadLocal<Future<RpcResponse>>();

    public static Object getResponse(long timeout) throws InterruptedException {
        if (null == futureThreadLocal.get()) {
            throw new RuntimeException("Thread [" + Thread.currentThread() + "] have not set the response future!");
        }
        try {
            RpcResponse response = futureThreadLocal.get().get(timeout, TimeUnit.MILLISECONDS);
            return response;
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException("Time out", e);
        }
    }

    public static void setFuture(Future<RpcResponse> future) {
        futureThreadLocal.set(future);
    }

}
