package com.fr.swift.query.info.remote;

import com.fr.swift.query.info.bean.parser.QueryInfoParser;
import com.fr.swift.query.info.bean.query.QueryInfoBean;
import com.fr.swift.query.info.bean.query.QueryInfoBeanFactory;
import com.fr.swift.query.query.QueryInfo;
import com.fr.swift.query.query.QueryType;
import com.fr.swift.source.SwiftResultSet;
import com.fr.third.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.net.URI;

/**
 * Created by Lyon on 2018/6/5.
 */
public class RemoteQueryInfoImpl<T extends SwiftResultSet> implements RemoteQueryInfo<T>, Serializable {

    private static final long serialVersionUID = -5148058795330646387L;
    private QueryType remoteQueryType;
    private String queryBeanJson;
    private transient QueryInfo<T> queryInfo;
    private transient ObjectMapper mapper = new ObjectMapper();

    public RemoteQueryInfoImpl(QueryType remoteQueryType, QueryInfo<T> queryInfo) {
        this.remoteQueryType = remoteQueryType;
        makeQueryJson(queryInfo);
    }

    private void makeQueryJson(QueryInfo<T> queryInfo) {
        QueryInfoBean queryInfoBean = QueryInfoBeanFactory.create(queryInfo);
        try {
            queryBeanJson = mapper.writeValueAsString(queryInfoBean);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private QueryInfo<T> createQueryInfo() {
        try {
            queryInfo = QueryInfoParser.parse(QueryInfoBeanFactory.create(queryBeanJson));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return queryInfo;
    }

    @Override
    public QueryInfo<T> getQueryInfo() {
        if (null != queryInfo) {
            return queryInfo;
        }
        return createQueryInfo();
    }

    @Override
    public String getQueryId() {
        if (null != queryInfo) {
            return queryInfo.getQueryId();
        }
        return createQueryInfo().getQueryId();
    }

    @Override
    public QueryType getType() {
        return remoteQueryType;
    }

    @Override
    public URI getQuerySegment() {
        if (null != queryInfo) {
            return queryInfo.getQuerySegment();
        }
        return createQueryInfo().getQuerySegment();
    }

    @Override
    public void setQuerySegment(URI target) {
        if (null == queryInfo) {
            queryInfo = createQueryInfo();
        }
        queryInfo.setQuerySegment(target);
        makeQueryJson(queryInfo);
    }

}
