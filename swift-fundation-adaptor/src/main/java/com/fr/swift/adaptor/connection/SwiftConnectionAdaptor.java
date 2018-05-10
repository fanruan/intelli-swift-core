package com.fr.swift.adaptor.connection;

import com.finebi.base.stable.StableManager;
import com.finebi.conf.exception.FineConnectRejectException;
import com.finebi.conf.service.connection.FineConnectionService;
import com.fr.data.impl.JDBCDatabaseConnection;
import com.fr.engine.bi.connection.ConnectionAdapter;
import com.fr.engine.constant.Null;

import java.sql.Connection;

/**
 * @author yee
 * @date 2018/4/9
 */
public class SwiftConnectionAdaptor implements ConnectionAdapter {

    private FineConnectionService fineConnectionService;

    @Override
    public Connection getConnection(String dbName) throws Exception {
        com.fr.data.impl.Connection fineConnection = getFineConnectionService().getConnectionByName(dbName);
        if (Null.isNull(fineConnection)) {
            throw new FineConnectRejectException(dbName);
        }
        return fineConnection.createConnection();
    }

    @Override
    public String getSchema(String dbName) throws Exception{
        com.fr.data.impl.Connection fineConnection = getFineConnectionService().getConnectionByName(dbName);
        return ((JDBCDatabaseConnection)fineConnection).getSchema();
    }

    private FineConnectionService getFineConnectionService() {
        if (null == fineConnectionService) {
            fineConnectionService = StableManager.getContext().getObject("fineConnectionService");
        }
        return fineConnectionService;
    }
}
