package com.fr.swift.cloud.query.info.bean.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fr.swift.cloud.query.info.bean.element.AggregationBean;
import com.fr.swift.cloud.query.info.bean.element.DimensionBean;
import com.fr.swift.cloud.query.info.bean.element.LimitBean;
import com.fr.swift.cloud.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.cloud.query.info.bean.post.PostQueryInfoBean;
import com.fr.swift.cloud.query.query.QueryType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by Lyon on 2018/6/2.
 */
public class GroupQueryInfoBean extends AbstractSingleTableQueryInfoBean implements Serializable {

    private static final long serialVersionUID = 8719563190390818951L;

    private GroupQueryInfoBean() {

    }

    @JsonProperty
    private List<AggregationBean> aggregations = new ArrayList<AggregationBean>(0);
    @JsonProperty
    private List<PostQueryInfoBean> postAggregations = new ArrayList<PostQueryInfoBean>(0);

    {
        queryType = QueryType.GROUP;
    }

    public List<AggregationBean> getAggregations() {
        return aggregations;
    }

    public void setAggregations(List<AggregationBean> aggregations) {
        this.aggregations = aggregations;
    }

    public List<PostQueryInfoBean> getPostAggregations() {
        return postAggregations;
    }

    public void setPostAggregations(List<PostQueryInfoBean> postAggregations) {
        this.postAggregations = postAggregations;
    }

    public static Builder builder(String table) {
        return new Builder(table);
    }

    public static class Builder {
        private GroupQueryInfoBean bean;

        public Builder(String table) {
            bean = new GroupQueryInfoBean();
            bean.setTableName(table);
        }

        public Builder setFetchSize(int fetchSize) {
            bean.setFetchSize(fetchSize);
            return this;
        }

        public Builder setFilter(FilterInfoBean filter) {
            bean.setFilter(filter);
            return this;
        }

        public Builder setLimit(LimitBean limit) {
            bean.setLimit(limit);
            return this;
        }

        public Builder setDimensions(List<DimensionBean> dimensions) {
            bean.setDimensions(dimensions);
            return this;
        }

        public Builder setDimensions(DimensionBean... dimensions) {
            bean.setDimensions(Arrays.asList(dimensions));
            return this;
        }

        public Builder setAggregations(List<AggregationBean> aggregations) {
            bean.setAggregations(aggregations);
            return this;
        }

        public Builder setAggregations(AggregationBean... aggregations) {
            bean.setAggregations(Arrays.asList(aggregations));
            return this;
        }

        public Builder setPostAggregations(List<PostQueryInfoBean> postAggregations) {
            bean.setPostAggregations(postAggregations);
            return this;
        }

        public GroupQueryInfoBean build() {
            if (bean.getQueryId() == null) {
                bean.setQueryId(UUID.randomUUID().toString());
            }
            return bean;
        }
    }
}
