package com.fr.swift.config.condition.impl;

import com.fr.swift.config.condition.SwiftConfigCondition;
import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.config.oper.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2019-08-04
 */
public class SwiftConfigConditionImpl implements SwiftConfigCondition {

    private List<Order> orders;
    private List<ConfigWhere> wheres;

    private SwiftConfigConditionImpl() {
        this.orders = new ArrayList<>();
        this.wheres = new ArrayList<>();
    }

    public static SwiftConfigCondition newInstance() {
        return new SwiftConfigConditionImpl();
    }

    @Override
    public SwiftConfigCondition addSort(Order order) {
        this.orders.add(order);
        return this;
    }

    @Override
    public SwiftConfigCondition addWhere(ConfigWhere where) {
        this.wheres.add(where);
        return this;
    }

    @Override
    public List<Order> getSort() {
        return orders;
    }

    @Override
    public List<ConfigWhere> getWheres() {
        return wheres;
    }
}
