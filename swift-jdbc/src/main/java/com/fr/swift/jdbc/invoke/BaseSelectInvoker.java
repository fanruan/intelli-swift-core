package com.fr.swift.jdbc.invoke;

import com.fr.swift.query.query.QueryBean;
import com.fr.swift.source.SwiftResultSet;

/**
 * @author yee
 * @date 2018/8/29
 */
public abstract class BaseSelectInvoker implements SqlInvoker<SwiftResultSet> {

    protected QueryBean queryBean;

    public BaseSelectInvoker(QueryBean queryBean) {
        this.queryBean = queryBean;
    }

    @Override
    public Type getType() {
        return Type.QUERY;
    }
}
