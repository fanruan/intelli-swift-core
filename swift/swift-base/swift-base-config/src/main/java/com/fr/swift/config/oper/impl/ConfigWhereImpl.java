package com.fr.swift.config.oper.impl;


import com.fr.swift.config.oper.ConfigWhere;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author yee
 * @date 2018-12-30
 */
public class ConfigWhereImpl<T> implements ConfigWhere<T> {
    private String column;
    private T value;
    private Type type;

    private ConfigWhereImpl(String column, T value, Type type) {
        this.column = column;
        this.value = value;
        this.type = type;
    }

    public static ConfigWhere<Serializable> eq(String column, Serializable value) {
        return new ConfigWhereImpl<Serializable>(column, value, Type.EQ);
    }

    public static ConfigWhere<Collection> in(String column, Collection value) {
        return new ConfigWhereImpl<Collection>(column, value, Type.IN);
    }

    public static ConfigWhere<String> like(String column, String value, MatchMode mode) {
        switch (mode) {
            case END:
                return new ConfigWhereImpl<String>(column, String.format("%%%s", value), Type.LIKE);
            case START:
                return new ConfigWhereImpl<String>(column, String.format("%s%%", value), Type.LIKE);
            default:
                return new ConfigWhereImpl<String>(column, String.format("%%%s%%", value), Type.LIKE);
        }
    }

    @Override
    public T getValue() {
        return value;
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
