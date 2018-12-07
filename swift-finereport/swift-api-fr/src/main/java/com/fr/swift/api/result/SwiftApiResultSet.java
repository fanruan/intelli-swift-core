package com.fr.swift.api.result;

import com.fr.swift.api.rpc.result.AbstractSwiftResultSet;
import com.fr.swift.api.rpc.session.impl.SwiftApiSessionImpl;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.query.result.serialize.SerializableDetailResultSet;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/8/27
 */
public class SwiftApiResultSet extends AbstractSwiftResultSet {
    private static final long serialVersionUID = 6857337351317783867L;
    private SwiftApiSessionImpl session;

    public SwiftApiResultSet(SerializableDetailResultSet resultSet, SwiftDatabase database, SwiftApiSessionImpl session) throws SQLException {
        super(resultSet, database);
        this.session = session;
    }

    @Override
    protected AbstractSwiftResultSet queryNextPage(String queryJson) throws Exception {
        return (AbstractSwiftResultSet) session.query(database, queryJson);
    }
}
