package com.fr.swift.query.query;

import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.util.qm.bool.BExpr;

import java.io.Serializable;

/**
 * Created by Lyon on 2018/7/20.
 */
public interface FilterBean<T> extends BExpr, Serializable {

    SwiftDetailFilterType getType();

    void setType(SwiftDetailFilterType type);

    T getFilterValue();

    void setFilterValue(T filterValue);
}
