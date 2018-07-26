package com.fr.swift.result.serialize;

import com.fr.swift.query.query.QueryRunnerProvider;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftNodeUtils;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.Crasher;

import java.sql.SQLException;
import java.util.Iterator;

/**
 * 对应QueryType.LOCAL_ALL（当前节点包含查询的所有segment，并且当前节点处理的是查询节点转发过来的请求）
 * <p>
 * Created by Lyon on 2018/6/14.
 */
public class LocalAllNodeResultSet implements NodeResultSet<SwiftNode>, SerializableResultSet {

    private static final long serialVersionUID = 7098094791977510417L;
    private int fetchSize;
    private String jsonString;
    private SwiftNode root;
    private boolean hasNextPage = true;
    private boolean originHasNextPage;
    private transient Iterator<Row> iterator;

    public LocalAllNodeResultSet(int fetchSize, String jsonString, SwiftNode root, boolean originHasNextPage) {
        this.fetchSize = fetchSize;
        this.jsonString = jsonString;
        this.root = root;
        this.originHasNextPage = originHasNextPage;
    }

    @Override
    public int getFetchSize() {
        return fetchSize;
    }

    @Override
    public SwiftNode<SwiftNode> getNode() {
        hasNextPage = false;
        if (originHasNextPage) {
            try {
                LocalAllNodeResultSet resultSet = (LocalAllNodeResultSet) QueryRunnerProvider.getInstance().executeRemoteQuery(jsonString, null);
                hasNextPage = true;
                this.root = resultSet.root;
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
        if (iterator == null) {
            iterator = SwiftNodeUtils.node2RowIterator(root);
        }
        return iterator.hasNext();
    }

    @Override
    public Row getNextRow() throws SQLException {
        return iterator.next();
    }

    @Override
    public void close() throws SQLException {

    }
}
