package com.fr.swift.query.result.serialize;

import com.fr.swift.result.BaseNodeResultSet;
import com.fr.swift.result.NodeMergeResultSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.source.Row;
import com.fr.swift.source.SerializableResultSet;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.structure.Pair;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 对应QueryType.LOCAL_PART（当前节点包含查询的所有segment，并且当前节点处理的是查询节点转发过来的请求）
 * <p>
 * Created by Lyon on 2018/6/14.
 */
public class LocalPartNodeResultSet extends BaseNodeResultSet<SwiftNode> implements NodeMergeResultSet<SwiftNode>, SerializableResultSet {

    private static final long serialVersionUID = -7163285398162627401L;
    private int fetchSize;
    private String jsonString;
    private Pair<SwiftNode, List<Map<Integer, Object>>> page;
    private boolean hasNextPage = true;
    private boolean originHasNextPage;

    public LocalPartNodeResultSet(int fetchSize, String jsonString, Pair<SwiftNode, List<Map<Integer, Object>>> page,
                                  boolean originHasNextPage) {
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
        Pair<SwiftNode, List<Map<Integer, Object>>> ret = null;
        if (hasNextPage) {
            hasNextPage = false;
            ret = page;
            page = null;
            return ret;
        }
        if (originHasNextPage) {
//            try {
//                AnalyseService service = ProxySelector.getInstance().getFactory().getProxy(AnalyseService.class);
//                LocalPartNodeResultSet resultSet = (LocalPartNodeResultSet) service.getRemoteQueryResult(jsonString, null);
//                ret = resultSet.getPage();
//                originHasNextPage = resultSet.hasNextPage();
//            } catch (SQLException e) {
//                Crasher.crash(e);
//            }
        }
        return ret;
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
