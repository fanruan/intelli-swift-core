package com.fr.swift.result.serialize;

import com.fr.swift.query.query.QueryBeanManager;
import com.fr.swift.query.query.QueryRunnerProvider;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeResultSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.structure.Pair;
import com.fr.swift.util.Crasher;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 对应QueryType.LOCAL_PART（当前节点包含查询的所有segment，并且当前节点处理的是查询节点转发过来的请求）
 * <p>
 * Created by Lyon on 2018/6/14.
 */
public class LocalPartNodeResultSet implements NodeMergeResultSet<SwiftNode>, SerializableResultSet {

    private static final long serialVersionUID = -7163285398162627401L;
    private String queryId;
    private SwiftNode root;
    private List<Map<Integer, Object>> dictionary;
    private boolean hasNextPage = true;
    private boolean originHasNextPage;

    public LocalPartNodeResultSet(String queryId, NodeMergeResultSet<GroupNode> resultSet) {
        this.queryId = queryId;
        this.root = resultSet.getNode();
        this.dictionary = resultSet.getRowGlobalDictionaries();
        this.originHasNextPage = resultSet.hasNextPage();
    }

    @Override
    public List<Map<Integer, Object>> getRowGlobalDictionaries() {
        return dictionary;
    }

    @Override
    public SwiftNode<SwiftNode> getNode() {
        hasNextPage = false;
        if (originHasNextPage) {
            // TODO: 2018/6/14 向远程节点拉取下一页数据
            Pair<String, SegmentDestination> pair = QueryBeanManager.getInstance().getPair(queryId);
            if (pair == null) {
                Crasher.crash("invalid remote queryInfo!");
            }
            try {
                LocalPartNodeResultSet resultSet = (LocalPartNodeResultSet) QueryRunnerProvider.getInstance().executeRemoteQuery(pair.getKey(), pair.getValue());
                hasNextPage = true;
                this.root = resultSet.root;
                this.dictionary = resultSet.dictionary;
                this.originHasNextPage = resultSet.originHasNextPage;
            } catch (SQLException e) {
                Crasher.crash(e);
            }
        }
        return root;
    }

    @Override
    public boolean hasNextPage() {
        return hasNextPage || originHasNextPage;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return null;
    }

    @Override
    public boolean hasNext() throws SQLException {
        return false;
    }

    @Override
    public Row getNextRow() throws SQLException {
        return null;
    }

    @Override
    public void close() throws SQLException {

    }
}
