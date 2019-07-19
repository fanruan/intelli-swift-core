package com.fr.swift.jdbc.creator;

import com.fr.swift.query.query.FilterBean;

/**
 * @author yee
 * @date 2019-07-19
 */
public interface FilterBeanCreator<T extends FilterBean> {
    /**
     * 创建FilterBean
     *
     * @param column
     * @param value
     * @return
     */
    T create(String column, Object value);

    /**
     * 关键字类型 Between的时候用
     *
     * @return
     */
    int type();
}
