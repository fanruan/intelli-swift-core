package com.fr.swift.netty.rpc.pool;

/**
 * This class created on 2018/8/1
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class AsyncRpcPool extends AbstractRpcPool {

    private AsyncRpcPool() {
        super(new AsyncRpcKeyPoolFactory());
    }

    private final static AsyncRpcPool INSTANCE = new AsyncRpcPool();

    public static AsyncRpcPool getIntance() {
        return INSTANCE;
    }
}
