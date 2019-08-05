package com.fr.swift.query.info.bean.element.filter.impl;

import com.fr.swift.query.filter.SwiftDetailFilterType;

import java.io.Serializable;

/**
 * @author yee
 * @date 2019-07-01
 */
public class WorkDayFilterInfoBean extends DetailFilterInfoBean implements Serializable {

    private static final long serialVersionUID = -2459114809208218122L;

    {
        type = SwiftDetailFilterType.WORK_DAY;
    }


    @Override
    public Object getFilterValue() {
        return null;
    }

    @Override
    public void setFilterValue(Object filterValue) {

    }
}
