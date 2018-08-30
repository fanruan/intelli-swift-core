package com.fr.swift.jdbc.session.impl;

import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.jdbc.exception.SwiftJDBCNotSupportedException;
import com.fr.swift.jdbc.mode.Mode;
import com.fr.swift.jdbc.rpc.RpcCaller;
import com.fr.swift.jdbc.rpc.holder.AddressHolder;
import com.fr.swift.jdbc.session.SwiftJdbcSession;
import com.fr.swift.jdbc.session.SwiftJdbcSessionFactory;
import com.fr.swift.util.Crasher;

/**
 * @author yee
 * @date 2018/8/27
 */
public class SwiftJdbcSessionFactoryImpl implements SwiftJdbcSessionFactory {

    protected AddressHolder holder;
    private SwiftDatabase schema;
    private Mode mode;

    public SwiftJdbcSessionFactoryImpl(SwiftDatabase schema, String host, int port) {
        holder = AddressHolder.getHolder(host, port);
        this.schema = schema;
        this.mode = Mode.SERVER;
    }

    public SwiftJdbcSessionFactoryImpl(SwiftDatabase schema) {
        this.schema = schema;
        this.mode = Mode.EMB;
    }

    @Override
    public SwiftJdbcSession openSession() {
        switch (mode) {
            case SERVER:
                return new SwiftJdbcServerSessionImpl(schema, RpcCaller.connectSelectService(holder.nextAnalyseAddress()), RpcCaller.connectMaintenanceService(holder.nextRealTimeAddress()));
            case EMB:
                return new SwiftJdbcEmbSessionImpl(schema);
            default:
                return Crasher.crash(new SwiftJDBCNotSupportedException(mode + " is unsupported"));
        }
    }
}
