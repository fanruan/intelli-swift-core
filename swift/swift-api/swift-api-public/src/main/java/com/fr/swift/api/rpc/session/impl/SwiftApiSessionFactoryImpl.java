package com.fr.swift.api.rpc.session.impl;

import com.fr.swift.api.rpc.Api;
import com.fr.swift.api.rpc.ApiServiceAddressHolder;
import com.fr.swift.api.rpc.pool.CallClientPool;
import com.fr.swift.api.rpc.session.SwiftApiSessionFactory;

/**
 * @author yee
 * @date 2018/8/27
 */
public class SwiftApiSessionFactoryImpl implements SwiftApiSessionFactory<SwiftApiSessionImpl> {

    protected ApiServiceAddressHolder holder;
    private int maxFrameSize;

    public SwiftApiSessionFactoryImpl(String address, int maxFrameSize) {
        holder = ApiServiceAddressHolder.getHolder(address);
        this.maxFrameSize = maxFrameSize;
    }

    public SwiftApiSessionFactoryImpl(String address) {
        this(address, Api.DEFAULT_MAX_FRAME_SIZE);
    }

    @Override
    public SwiftApiSessionImpl openSession() {
        return new SwiftApiSessionImpl(Api.connectSelectApi(holder.nextAnalyseAddress(), maxFrameSize),
                Api.connectDataMaintenanceApi(holder.nextRealTimeAddress(), maxFrameSize));
    }

    @Override
    public void close() {
        CallClientPool.getInstance(maxFrameSize).close();
    }
}
