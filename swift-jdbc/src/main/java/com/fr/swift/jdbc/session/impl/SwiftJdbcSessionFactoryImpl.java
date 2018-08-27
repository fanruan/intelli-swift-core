package com.fr.swift.jdbc.session.impl;

import com.fr.swift.db.Schema;
import com.fr.swift.jdbc.rpc.RpcCaller;
import com.fr.swift.jdbc.rpc.holder.AddressHolder;
import com.fr.swift.jdbc.session.SwiftJdbcSession;
import com.fr.swift.jdbc.session.SwiftJdbcSessionFactory;

/**
 * @author yee
 * @date 2018/8/27
 */
public class SwiftJdbcSessionFactoryImpl implements SwiftJdbcSessionFactory {

    protected AddressHolder holder;
    private Schema schema;

    public SwiftJdbcSessionFactoryImpl(Schema schema, String address) {
        holder = AddressHolder.getHolder(address);
        this.schema = schema;
    }

    public SwiftJdbcSessionFactoryImpl(Schema schema, String host, int port) {
        holder = AddressHolder.getHolder(host, port);
        this.schema = schema;
    }

    @Override
    public SwiftJdbcSession openSession() {
        return new SwiftJdbcSessionImpl(schema, RpcCaller.connectSelectService(holder.nextAnalyseAddress()), RpcCaller.connectMaintenanceService(holder.nextRealTimeAddress()));
    }
}
