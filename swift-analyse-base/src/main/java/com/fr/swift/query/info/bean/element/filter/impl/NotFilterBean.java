package com.fr.swift.query.info.bean.element.filter.impl;

import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.util.qm.bool.BExpr;
import com.fr.swift.util.qm.bool.BExprType;
import com.fr.swift.util.qm.bool.BUExpr;

import java.io.Serializable;

/**
 * Created by Lyon on 2018/6/28.
 */
public class NotFilterBean extends GeneralFilterInfoBean<FilterInfoBean> implements BUExpr, Serializable {

    private static final long serialVersionUID = -1060322652409398537L;

    {
        type = SwiftDetailFilterType.NOT;
    }

    public NotFilterBean() {
    }

    public NotFilterBean(FilterInfoBean filter) {
        filterValue = filter;
    }

    @Override
    public FilterInfoBean getFilterValue() {
        return filterValue;
    }

    @Override
    public void setFilterValue(FilterInfoBean filterValue) {
        this.filterValue = filterValue;
    }

    @Override
    public BExpr getChild() {
        return filterValue;
    }

    @Override
    public BExprType type() {
        return BExprType.NOT;
    }
}
