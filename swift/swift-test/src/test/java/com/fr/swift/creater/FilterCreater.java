package com.fr.swift.creater;

import com.fr.swift.query.info.bean.element.filter.impl.InFilterBean;
import com.fr.swift.query.query.FilterBean;

import java.util.Collections;

/**
 * This class created on 2018/9/18
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class FilterCreater {
    public static FilterBean createEqualFilter(String fieldName, String value) {
        InFilterBean bean = new InFilterBean();
        bean.setColumn(fieldName);
        bean.setFilterValue(Collections.singleton(value));
        return bean;
    }
}
