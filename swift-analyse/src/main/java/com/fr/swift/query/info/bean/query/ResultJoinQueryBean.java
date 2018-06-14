package com.fr.swift.query.info.bean.query;

import com.fr.swift.query.info.bean.post.PostQueryInfoBean;
import com.fr.swift.query.query.QueryType;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Lyon on 2018/6/7.
 */
public class ResultJoinQueryBean extends AbstractQueryBean {

    @JsonProperty
    private List<QueryBean> queryBeans;
    @JsonProperty
    private List<String> joinedFields;
    @JsonProperty
    private List<PostQueryInfoBean> postQueryInfoBeans;

    public List<QueryBean> getQueryBeans() {
        return queryBeans;
    }

    public void setQueryBeans(List<QueryBean> queryBeans) {
        this.queryBeans = queryBeans;
    }

    public List<String> getJoinedFields() {
        return joinedFields;
    }

    public void setJoinedFields(List<String> joinedFields) {
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
