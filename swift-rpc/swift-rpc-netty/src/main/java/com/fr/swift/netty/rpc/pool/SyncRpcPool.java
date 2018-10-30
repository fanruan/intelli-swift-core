package com.fr.swift.netty.rpc.pool;

/**
 * This class created on 2018/8/1
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SyncRpcPool extends AbstractRpcPool {

    private final static SyncRpcPool INSTANCE = new SyncRpcPool();

    private SyncRpcPool() {
        super(new SyncRpcKeyPoolFactory());
    }

    public static SyncRpcPool getInstance() {
        return INSTANCE;
    }

}
