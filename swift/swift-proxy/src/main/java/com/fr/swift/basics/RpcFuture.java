package com.fr.swift.basics;

import java.util.concurrent.Future;

/**
 * This class created on 2018/8/14
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface RpcFuture<T> extends Future<Object> {
    void done(T t);

    RpcFuture addCallback(AsyncRpcCallback callback);
}
