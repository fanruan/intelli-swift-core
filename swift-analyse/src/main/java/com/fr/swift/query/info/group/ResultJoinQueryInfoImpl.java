package com.fr.swift.query.info.group;

import com.fr.swift.query.QueryInfo;
import com.fr.swift.query.QueryType;
import com.fr.swift.query.info.ResultJoinQueryInfo;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.source.SwiftResultSet;

import java.util.List;

/**
 * Created by Lyon on 2018/5/31.
 */
public class ResultJoinQueryInfoImpl<T extends SwiftResultSet> implements ResultJoinQueryInfo<T> {

    private String queryId;
    private List<QueryInfo<T>> queryInfoList;
    private List<Dimension> dimensions;

    public ResultJoinQueryInfoImpl(String queryId, List<QueryInfo<T>> queryInfoList, List<Dimension> dimensions) {
        this.queryId = queryId;
        this.queryInfoList = queryInfoList;
        this.dimensions = dimensions;
    }

    @Override
    public List<QueryInfo<T>> getQueryInfoList() {
        return queryInfoList;
    }

    @Override
    public List<Dimension> getJoinedDimensions() {
        return dimensions;
    }

    @Override
    public String getQueryId() {
        return queryId;
    }

    @Override
    public QueryType getType() {
        return QueryType.RESULT_JOIN;
    }
}
