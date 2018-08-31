package com.fr.swift.jdbc.result;

import com.fr.swift.api.rpc.result.AbstractSwiftResultSet;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.jdbc.rpc.RpcCaller;
import com.fr.swift.result.serialize.SerializableDetailResultSet;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/8/27
 */
public class SwiftPaginationResultSet extends AbstractSwiftResultSet {
    private RpcCaller.SelectRpcCaller caller;

    public SwiftPaginationResultSet(SerializableDetailResultSet resultSet, RpcCaller.SelectRpcCaller caller, SwiftDatabase database) throws SQLException {
        super(resultSet, database);
        this.caller = caller;
    }

    @Override
    protected AbstractSwiftResultSet queryNextPage(String queryJson) {
        try {
            return (SwiftPaginationResultSet) caller.query(database, getJsonString());
        } catch (Exception e) {
            return null;
        }
    }
}
