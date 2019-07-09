package com.fr.swift.query.query;

import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.util.qm.bool.BExpr;

/**
 * Created by Lyon on 2018/7/20.
 */
public interface FilterBean<T> extends BExpr {


    /**
     * 获取过滤器类型
     * 如：select * from test_table where id in (1, 2, 3)
     * 此方法返回IN
     *
     * @return
     */
    SwiftDetailFilterType getType();

    void setType(SwiftDetailFilterType type);

    /**
     * 获取过滤器值
     * 如：select * from test_table where id in (1, 2, 3)
     * 则此方法返回 Set(1,2,3)
     * @return
     */
    T getFilterValue();

    void setFilterValue(T filterValue);
}
