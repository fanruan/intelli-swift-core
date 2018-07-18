package com.fr.swift.result.serialize;

import com.fr.swift.query.query.QueryRunnerProvider;
import com.fr.swift.result.NodeMergeResultSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
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
    private String jsonString;
    private SwiftNode root;
    private List<Map<Integer, Object>> dictionary;
    private boolean hasNextPage = true;
    private boolean originHasNextPage;

    public LocalPartNodeResultSet(String jsonString, SwiftNode root, List<Map<Integer, Object>> dictionary,
                                  boolean originHasNextPage) {
        this.jsonString = jsonString;
        this.root = root;
        this.dictionary = dictionary;
        this.originHasNextPage = originHasNextPage;
    }

    @Override
    public List<Map<Integer, Object>> getRowGlobalDictionaries() {
        return dictionary;
    }

    @Override
    public SwiftNode<SwiftNode> getNode() {
        hasNextPage = false;
        if (originHasNextPage) {
            try {
                LocalPartNodeResultSet resultSet = (LocalPartNodeResultSet) QueryRunnerProvider.getInstance().executeRemoteQuery(jsonString, null);
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
