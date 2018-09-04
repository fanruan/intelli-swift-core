package com.fr.swift.jdbc.metadata.server;

import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.jdbc.metadata.AbstractTableMetaDataGetter;
import com.fr.swift.jdbc.rpc.RpcCaller;
import com.fr.swift.source.SwiftMetaData;

/**
 * @author yee
 * @date 2018/9/4
 */
public class ServerMetaDataGetter extends AbstractTableMetaDataGetter {
    private RpcCaller caller;

    public ServerMetaDataGetter(SwiftDatabase database, String tableName, RpcCaller caller) {
        super(database, tableName);
        this.caller = caller;
    }

    @Override
    public SwiftMetaData get() {
        return caller.detectiveMetaData(database, tableName);
    }
}
