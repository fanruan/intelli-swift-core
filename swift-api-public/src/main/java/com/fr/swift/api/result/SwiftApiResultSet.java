package com.fr.swift.api.result;

import com.fr.swift.api.rpc.result.AbstractSwiftResultSet;
import com.fr.swift.api.rpc.session.impl.SwiftApiSessionImpl;
import com.fr.swift.result.serialize.SerializableDetailResultSet;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/8/27
 */
public class SwiftApiResultSet extends AbstractSwiftResultSet {
    private SwiftApiSessionImpl session;

    public SwiftApiResultSet(SerializableDetailResultSet resultSet, SwiftApiSessionImpl session) throws SQLException {
        super(resultSet);
        this.session = session;
    }

    @Override
    protected AbstractSwiftResultSet queryNextPage(String queryJson) {
        return (AbstractSwiftResultSet) session.query(queryJson);
    }
}
