package com.fr.swift.config.condition;

import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.config.oper.Order;

import java.util.List;

/**
 * @author yee
 * @date 2019-07-30
 */
public interface SwiftConfigCondition {
    /**
     * 添加排序
     *
     * @param order
     * @return
     */
    SwiftConfigCondition addSort(Order order);

    /**
     * and
     *
     * @param where
     * @return
     */
    SwiftConfigCondition addWhere(ConfigWhere where);

    /**
     * 获取排序
     *
     * @return
     */
    List<Order> getSort();

    /**
     * 获取where
     *
     * @return
     */
    List<ConfigWhere> getWheres();
}
