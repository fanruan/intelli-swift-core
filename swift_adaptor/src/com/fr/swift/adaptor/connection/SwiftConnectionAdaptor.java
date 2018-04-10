package com.fr.swift.adaptor.connection;

import com.finebi.base.stable.StableManager;
import com.finebi.conf.exception.FineConnectRejectException;
import com.finebi.conf.service.connection.FineConnectionService;
import com.fr.engine.bi.connection.ConnectionAdapter;
import com.fr.engine.constant.Null;

import java.sql.Connection;

/**
 * @author yee
 * @date 2018/4/9
 */
public class SwiftConnectionAdaptor implements ConnectionAdapter {
    FineConnectionService fineConnectionService = (FineConnectionService) StableManager.getContext().getObject("fineConnectionService");

    @Override
    public Connection getConnection(String dbName) throws Exception {
        com.fr.data.impl.Connection fineConnection = fineConnectionService.getConnectionByName(dbName);
        if (Null.isNull(fineConnection)) {
            throw new FineConnectRejectException(dbName);
        }
        return fineConnection.createConnection();
    }
}
