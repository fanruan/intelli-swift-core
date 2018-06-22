package com.fr.swift.query.info.bean.query;

import com.fr.swift.query.query.QueryType;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.structure.Pair;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

import java.util.Comparator;
import java.util.List;

/**
 * Created by Lyon on 2018/6/3.
 */
public class DetailQueryInfoBean extends AbstractSingleTableQueryInfoBean {

    @JsonProperty
    private SwiftMetaData metaData;
    @JsonProperty
    private List<Pair<Integer, Comparator>> comparators;

    public SwiftMetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(SwiftMetaData metaData) {
        this.metaData = metaData;
    }

    public List<Pair<Integer, Comparator>> getComparators() {
        return comparators;
    }

    public void setComparators(List<Pair<Integer, Comparator>> comparators) {
        this.comparators = comparators;
    }

    @Override
    public QueryType getQueryType() {
        return QueryType.DETAIL;
    }
}
