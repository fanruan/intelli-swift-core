package com.fr.swift.jdbc.invoke.emb;

import com.fr.swift.jdbc.invoke.BaseSelectInvoker;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.query.QueryRunnerProvider;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/8/29
 */
public class EmbSelectInvoker extends BaseSelectInvoker {
    public EmbSelectInvoker(QueryBean queryBean) {
        super(queryBean);
    }

    @Override
    public SwiftResultSet invoke() throws SQLException {
        try {
            return QueryRunnerProvider.getInstance().executeQuery(queryBean);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
}
