package com.fr.swift.query.post;

import com.fr.swift.query.info.group.GroupQueryInfo;
import com.fr.swift.query.post.utils.SwiftMetaDataUtils;
import com.fr.swift.query.query.Query;
import com.fr.swift.result.ChainedNodeResultSet;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftNodeOperator;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;

/**
 * Created by Lyon on 2018/6/1.
 */
public class PrepareMetaDataQuery extends AbstractPostQuery<NodeResultSet> {

    private Query<NodeResultSet> query;
    private GroupQueryInfo queryInfo;

    public PrepareMetaDataQuery(Query<NodeResultSet> query, GroupQueryInfo queryInfo) {
        this.query = query;
        this.queryInfo = queryInfo;
    }

    @Override
    public NodeResultSet getQueryResult() throws SQLException {
        SwiftMetaData metaData = SwiftMetaDataUtils.createMetaData(queryInfo);
        return new ChainedNodeResultSet(new SwiftNodeOperator<SwiftNode>() {
            @Override
            public SwiftNode operate(SwiftNode... node) {
                return node[0];
            }
        }, query.getQueryResult(), metaData);
    }
}
