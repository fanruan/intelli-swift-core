package com.fr.swift.config.oper.impl;

import com.fr.swift.config.oper.ConfigAggregation;

/**
 * @author Heng.J
 * @date 2019/12/24
 * @description
 * @since swift 1.1
 */
public class ConfigAggregationImpl implements ConfigAggregation {
    private String column;
    private Type type;

    public ConfigAggregationImpl(String column, Type type) {
        this.column = column;
        this.type = type;
    }

    public static ConfigAggregation max(String column) {
        return new ConfigAggregationImpl(column, Type.MAX);
    }

    public static ConfigAggregation min(String column) {
        return new ConfigAggregationImpl(column, Type.MIN);
    }

    @Override
    public String getColumn() {
        return column;
    }

    @Override
    public Type type() {
        return type;
    }
}
