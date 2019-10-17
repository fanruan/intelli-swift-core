package com.fr.swift.config.oper.impl;


import com.fr.swift.config.oper.Order;

/**
 * @author yee
 * @date 2018-12-30
 */
public class OrderImpl implements Order {
    private String column;
    private boolean asc;

    private OrderImpl(String column, boolean asc) {
        this.column = column;
        this.asc = asc;
    }

    public static Order asc(String column) {
        return new OrderImpl(column, true);
    }

    public static Order desc(String column) {
        return new OrderImpl(column, false);
    }

    @Override
    public boolean isAsc() {
        return asc;
    }

    @Override
    public String getColumn() {
        return column;
    }
}
