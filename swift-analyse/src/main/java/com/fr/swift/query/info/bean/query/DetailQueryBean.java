package com.fr.swift.query.info.bean.query;

import com.fr.swift.query.QueryType;

/**
 * Created by Lyon on 2018/6/3.
 */
public class DetailQueryBean extends AbstractSingleTableQueryBean {

    @Override
    public QueryType getQueryType() {
        return QueryType.DETAIL;
    }
}
