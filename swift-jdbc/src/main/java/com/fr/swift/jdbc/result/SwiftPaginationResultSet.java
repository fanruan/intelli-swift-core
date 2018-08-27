package com.fr.swift.jdbc.result;

import com.fr.swift.jdbc.rpc.RpcCaller;
import com.fr.swift.result.serialize.SerializableDetailResultSet;
import com.fr.swift.source.Row;
import com.fr.swift.util.Crasher;

import java.sql.SQLException;
import java.util.List;

/**
 * @author yee
 * @date 2018/8/27
 */
public class SwiftPaginationResultSet extends SerializableDetailResultSet {
    private RpcCaller.SelectRpcCaller caller;

    public SwiftPaginationResultSet(SerializableDetailResultSet resultSet, RpcCaller.SelectRpcCaller caller) throws SQLException {
        super(resultSet.getJsonString(), resultSet.getMetaData(), resultSet.getRows(), resultSet.isOriginHasNextPage(), resultSet.getRowCount());
        this.caller = caller;
    }

    @Override
    public List<Row> getPage() {
        hasNextPage = false;
        List<Row> ret = rows;
        if (originHasNextPage) {
            try {
                SwiftPaginationResultSet resultSet = (SwiftPaginationResultSet) caller.query(getJsonString());
                if (null != resultSet) {
                    hasNextPage = true;
                    this.rows = resultSet.getRows();
                    this.originHasNextPage = resultSet.isOriginHasNextPage();
                } else {
                    hasNextPage = false;
                    this.originHasNextPage = false;
                }
            } catch (Exception e) {
                Crasher.crash(e);
            }
        }
        return ret;
    }
}
