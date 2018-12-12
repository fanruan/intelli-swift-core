package com.fr.swift.query.result.serialize;

import com.fr.swift.result.BaseNodeResultSet;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.source.Row;
import com.fr.swift.source.SerializableResultSet;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.structure.Pair;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 对应QueryType.LOCAL_ALL（当前节点包含查询的所有segment，并且当前节点处理的是查询节点转发过来的请求）
 * <p>
 * Created by Lyon on 2018/6/14.
 */
public class LocalAllNodeResultSet extends BaseNodeResultSet<SwiftNode> implements NodeResultSet<SwiftNode>, SerializableResultSet {

    private static final long serialVersionUID = 7098094791977510417L;
    private int fetchSize;
    private String jsonString;
    private List<Row> page;
    private boolean originHasNextPage;
    private transient Iterator<Row> iterator;
    private transient SwiftMetaData metaData;

    public LocalAllNodeResultSet(int fetchSize, String jsonString, List<Row> page, boolean originHasNextPage) {
        this.fetchSize = fetchSize;
        this.jsonString = jsonString;
        this.page = page;
        this.originHasNextPage = originHasNextPage;
    }

    @Override
    public int getFetchSize() {
        return fetchSize;
    }

    @Override
    public Pair<SwiftNode, List<Map<Integer, Object>>> getPage() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasNextPage() {
        throw new UnsupportedOperationException();
    }

    public void setMetaData(SwiftMetaData metaData) {
        this.metaData = metaData;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return metaData;
    }

    @Override
    public boolean hasNext() throws SQLException {
        if (iterator == null) {
            iterator = page.iterator();
            page = null;
        }
        if (iterator.hasNext()) {
            return true;
        } else if (originHasNextPage) {
//            try {
//                AnalyseService service = ProxySelector.getInstance().getFactory().getProxy(AnalyseService.class);
//                LocalAllNodeResultSet resultSet = (LocalAllNodeResultSet) service.getRemoteQueryResult(jsonString, null);
//                if (resultSet != null && resultSet.page != null) {
//                    this.iterator = resultSet.page.iterator();
//                    this.originHasNextPage = resultSet.originHasNextPage;
//                }
//            } catch (SQLException e) {
//                Crasher.crash(e);
//            }
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
