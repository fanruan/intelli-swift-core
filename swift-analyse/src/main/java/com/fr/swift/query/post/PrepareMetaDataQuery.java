package com.fr.swift.query.post;

import com.fr.swift.query.info.group.GroupQueryInfo;
import com.fr.swift.query.post.utils.SwiftMetaDataUtils;
import com.fr.swift.query.query.Query;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftNodeOperator;
import com.fr.swift.result.node.resultset.ChainedNodeResultSet;
import com.fr.swift.result.node.resultset.FakeNodeResultSet;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.structure.Pair;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

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
        NodeResultSet resultSet = query.getQueryResult();
        if (resultSet instanceof FakeNodeResultSet) {
            // TODO: 2018/7/4 行排序破坏树结构的情况下，先直接取出resultSet返回了。这个要和resultSet内部的缓存翻页机制实现一起考虑
            return resultSet;
        }
        return new ChainedNodeResultSet(new SwiftNodeOperator() {
            @Override
            public Pair<SwiftNode, List<Map<Integer, Object>>> apply(Pair<? extends SwiftNode, List<Map<Integer, Object>>> p) {
                return Pair.of(p.getKey(), p.getValue());
            }
        }, resultSet, metaData);
    }
}
