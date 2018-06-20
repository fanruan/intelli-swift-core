package com.fr.swift.query.info.group;

import com.fr.swift.query.info.ResultJoinQueryInfo;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.info.group.post.PostQueryInfo;
import com.fr.swift.query.query.QueryInfo;
import com.fr.swift.query.query.QueryType;
import com.fr.swift.source.SwiftResultSet;

import java.net.URI;
import java.util.List;

/**
 * Created by Lyon on 2018/5/31.
 */
public class ResultJoinQueryInfoImpl<T extends SwiftResultSet> implements ResultJoinQueryInfo<T> {

    private String queryId;
    private List<QueryInfo<T>> queryInfoList;
    private List<Dimension> dimensions;
    private List<PostQueryInfo> postQueryInfoList;

    public ResultJoinQueryInfoImpl(String queryId, List<QueryInfo<T>> queryInfoList,
                                   List<Dimension> dimensions, List<PostQueryInfo> postQueryInfoList) {
        this.queryId = queryId;
        this.queryInfoList = queryInfoList;
        this.dimensions = dimensions;
        this.postQueryInfoList = postQueryInfoList;
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
    public List<PostQueryInfo> getPostQueryInfoList() {
        return postQueryInfoList;
    }

    @Override
    public String getQueryId() {
        return queryId;
    }

    @Override
    public QueryType getType() {
        return QueryType.RESULT_JOIN;
    }

    @Override
    public URI getQuerySegment() {
        return null;
    }

    @Override
    public void setQuerySegment(URI order) {

    }

}
