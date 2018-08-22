package com.fr.swift.query.post;

import com.fr.swift.query.post.utils.SwiftMetaDataUtils;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.query.QueryType;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.node.resultset.Node2RowResultSet;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;

/**
 * Created by Lyon on 2018/6/1.
 */
public class PrepareMetaDataQuery<T extends SwiftResultSet> extends AbstractPostQuery<T> {

    private Query<T> query;
    private QueryBean bean;

    public PrepareMetaDataQuery(Query<T> query, QueryBean bean) {
        this.query = query;
        this.bean = bean;
    }

    @Override
    public T getQueryResult() throws SQLException {
        SwiftMetaData metaData = SwiftMetaDataUtils.createMetaData(bean);
        T resultSet = query.getQueryResult();
        if (bean.getQueryType() == QueryType.GROUP) {
            return (T) new Node2RowResultSet((NodeResultSet) resultSet, metaData);
        } else {
            ((DetailResultSet) resultSet).setMetaData(metaData);
            return resultSet;
        }
    }
}
