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

    public SwiftApiSessionFactoryImpl(String address) {
        holder = InternalServiceAddressHolder.getHolder(address);
    }

    @Override
    public SwiftApiSessionImpl openSession() {
        return new SwiftApiSessionImpl(Api.connectSelectApi(holder.nextAnalyseAddress()),
                Api.connectDataMaintenanceApi(holder.nextRealTimeAddress()));
    }
}
