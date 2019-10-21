package com.fr.swift.query.info.bean.element.filter.impl;

import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;

import java.util.List;

/**
 * @author yee
 * @date 2019-07-25
 */
public abstract class ComplexFilterInfoBean extends GeneralFilterInfoBean<List<FilterInfoBean>> {
    public static AndFilterBean and(List<FilterInfoBean> filters) {
        return new AndFilterBean(filters);
    }

    public static OrFilterBean or(List<FilterInfoBean> filters) {
        return new OrFilterBean(filters);
    }
}
