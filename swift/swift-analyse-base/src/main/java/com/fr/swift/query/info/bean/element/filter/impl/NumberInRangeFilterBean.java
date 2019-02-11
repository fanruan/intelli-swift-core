package com.fr.swift.query.info.bean.element.filter.impl;

import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.info.bean.element.filter.impl.value.RangeFilterValueBean;

import java.io.Serializable;

/**
 * Created by Lyon on 2018/6/28.
 */
public class NumberInRangeFilterBean extends DetailFilterInfoBean<RangeFilterValueBean> implements Serializable {

    private static final long serialVersionUID = 4119767705951357179L;

    {
        type = SwiftDetailFilterType.NUMBER_IN_RANGE;
    }

    @Override
    public RangeFilterValueBean getFilterValue() {
        return filterValue;
    }

    @Override
    public void setFilterValue(RangeFilterValueBean filterValue) {
        this.filterValue = filterValue;
    }

    public static Builder builder(String column) {
        return new Builder(column);
    }

    public static class Builder {
        private String column;
        private RangeFilterValueBean bean;

        public Builder(String column) {
            this.column = column;
            this.bean = new RangeFilterValueBean();
        }

        public Builder setStart(String start, boolean included) {
            bean.setStartIncluded(included);
            bean.setStart(start);
            return this;
        }

        public Builder setEnd(String end, boolean included) {
            bean.setStartIncluded(included);
            bean.setStart(end);
            return this;
        }

        public NumberInRangeFilterBean build() {
            NumberInRangeFilterBean filterBean = new NumberInRangeFilterBean();
            filterBean.setColumn(column);
            filterBean.setFilterValue(bean);
            return filterBean;
        }
    }
}
