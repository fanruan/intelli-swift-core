package com.fr.swift.query.info.bean.parser.funnel;

import com.fr.swift.query.info.bean.query.FunnelQueryBean;
import com.fr.swift.query.query.QueryType;
import com.fr.swift.result.FunnelResultSet;

import java.util.Set;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public class FunnelQueryInfoImpl implements FunnelQueryInfo<FunnelResultSet> {

    private String queryId;
    private Set<String> querySegments;
    private FunnelQueryBean bean;

    public FunnelQueryInfoImpl(String queryId, Set<String> querySegments, FunnelQueryBean bean) {
        this.queryId = queryId;
        this.querySegments = querySegments;
        this.bean = bean;
    }

    @Override
    public String getQueryId() {
        return queryId;
    }

    @Override
    public QueryType getType() {
        return QueryType.FUNNEL;
    }

    @Override
    public int getFetchSize() {
        return 10000;
    }

    @Override
    public Set<String> getQuerySegment() {
        return querySegments;
    }

    @Override
    public void setQuerySegment(Set<String> target) {
        this.querySegments = target;
    }

    @Override
    public FunnelQueryBean getQueryBean() {
        return bean;
    }
}
