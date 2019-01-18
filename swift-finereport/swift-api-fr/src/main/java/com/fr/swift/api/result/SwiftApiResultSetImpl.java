package com.fr.swift.api.result;

import com.fr.swift.api.rpc.session.impl.SwiftApiSessionImpl;
import com.fr.swift.db.SwiftDatabase;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/8/27
 */
public class SwiftApiResultSetImpl extends BaseApiResultSet<String> {
    private static final long serialVersionUID = 6857337351317783867L;
    private SwiftApiSessionImpl session;
    private SwiftDatabase database;

    public SwiftApiResultSetImpl(SwiftApiResultSet resultSet, SwiftDatabase database, SwiftApiSessionImpl session, String queryJson) throws SQLException {
        super(queryJson, resultSet.getMetaData(), resultSet.getRows(), resultSet.getRowCount(), resultSet.isOriginHasNextPage());
        this.database = database;
        this.session = session;
    }

    @Override
    public SwiftApiResultSet queryNextPage(String queryJson) throws SQLException {
        try {
            return session.query(database, queryJson);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
}
