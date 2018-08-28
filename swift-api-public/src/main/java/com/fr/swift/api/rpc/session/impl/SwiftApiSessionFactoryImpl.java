package com.fr.swift.api.rpc.session.impl;

import com.fr.swift.api.Api;
import com.fr.swift.api.rpc.holder.InternalServiceAddressHolder;
import com.fr.swift.api.rpc.session.SwiftApiSessionFactory;

/**
 * @author yee
 * @date 2018/8/27
 */
public class SwiftApiSessionFactoryImpl implements SwiftApiSessionFactory<SwiftApiSessionImpl> {

    protected InternalServiceAddressHolder holder;
    private int maxFrameSize;

    public SwiftApiSessionFactoryImpl(String address, int maxFrameSize) {
        holder = InternalServiceAddressHolder.getHolder(address);
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
}
