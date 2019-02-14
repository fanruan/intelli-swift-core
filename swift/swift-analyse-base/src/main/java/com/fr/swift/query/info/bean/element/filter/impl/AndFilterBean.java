package com.fr.swift.query.info.bean.element.filter.impl;

import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.util.qm.bool.BExpr;
import com.fr.swift.util.qm.bool.BExprType;
import com.fr.swift.util.qm.bool.BNExpr;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Lyon on 2018/6/28.
 */
public class AndFilterBean extends GeneralFilterInfoBean<List<FilterInfoBean>> implements BNExpr, Serializable {

    private static final long serialVersionUID = 7080214690415176381L;

    {
        type = SwiftDetailFilterType.AND;
    }

    public AndFilterBean() {
    }

    public AndFilterBean(List<FilterInfoBean> filters) {
        filterValue = filters;
    }

    @Override
    public List<FilterInfoBean> getFilterValue() {
        return filterValue;
    }

    @Override
    public void setFilterValue(List<FilterInfoBean> filterValue) {
        this.filterValue = filterValue;
    }

    @Override
    public List<? extends BExpr> getChildrenExpr() {
        return filterValue;
    }

    @Override
    public BExprType type() {
        return BExprType.AND;
    }
}
