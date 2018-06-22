package com.fr.swift.query.info.bean.query;

import com.fr.swift.query.info.bean.element.DimensionBean;
import com.fr.swift.query.info.bean.post.PostQueryInfoBean;
import com.fr.swift.query.query.QueryType;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Lyon on 2018/6/7.
 */
public class ResultJoinQueryInfoBean extends AbstractQueryInfoBean {

    @JsonProperty
    private List<QueryInfoBean> queryInfoBeans;
    @JsonProperty
    private List<DimensionBean> joinedFields;
    @JsonProperty
    private List<PostQueryInfoBean> postQueryInfoBeans;

    public List<QueryInfoBean> getQueryInfoBeans() {
        return queryInfoBeans;
    }

    public void setQueryInfoBeans(List<QueryInfoBean> queryInfoBeans) {
        this.queryInfoBeans = queryInfoBeans;
    }

    public List<DimensionBean> getJoinedFields() {
        return joinedFields;
    }

    public void setJoinedFields(List<DimensionBean> joinedFields) {
        this.joinedFields = joinedFields;
    }

    public List<PostQueryInfoBean> getPostQueryInfoBeans() {
        return postQueryInfoBeans;
    }

    public void setPostQueryInfoBeans(List<PostQueryInfoBean> postQueryInfoBeans) {
        this.postQueryInfoBeans = postQueryInfoBeans;
    }

    @Override
    public QueryType getQueryType() {
        return QueryType.RESULT_JOIN;
    }
}
