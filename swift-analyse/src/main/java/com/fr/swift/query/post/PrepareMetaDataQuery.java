package com.fr.swift.query.post;

import com.fr.swift.query.info.GroupQueryInfo;
import com.fr.swift.query.post.utils.SwiftMetaDataUtils;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.NodeResultSetImpl;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;

/**
 * Created by Lyon on 2018/6/1.
 */
public class PrepareMetaDataQuery extends AbstractPostQuery<NodeResultSet> {

    private PostQuery<NodeResultSet> query;
    private GroupQueryInfo queryInfo;

    public PrepareMetaDataQuery(PostQuery<NodeResultSet> query, GroupQueryInfo queryInfo) {
        this.query = query;
        this.queryInfo = queryInfo;
    }

    @Override
    public NodeResultSet getQueryResult() throws SQLException {
        SwiftMetaData metaData = SwiftMetaDataUtils.createMetaData(queryInfo);
        return new NodeResultSetImpl(queryInfo.getDimensions().size(), query.getQueryResult().getNode(), metaData);
    }
}
