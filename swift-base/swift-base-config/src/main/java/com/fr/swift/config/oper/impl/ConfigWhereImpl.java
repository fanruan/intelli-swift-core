package com.fr.swift.config.oper.impl;


import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.util.Strings;

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

    public static ConfigWhere<Number> gt(String column, Number value) {
        return new ConfigWhereImpl<>(column, value, Type.GT);
    }

    public static ConfigWhere<Serializable> eq(String column, Serializable value) {
        return new ConfigWhereImpl<>(column, value, Type.EQ);
    }

    public static ConfigWhere<Collection> in(String column, Collection value) {
        return new ConfigWhereImpl<>(column, value, Type.IN);
    }

    public static ConfigWhere<String> like(String column, String value, MatchMode mode) {
        switch (mode) {
            case END:
                return new ConfigWhereImpl<>(column, String.format("%%%s", value), Type.LIKE);
            case START:
                return new ConfigWhereImpl<>(column, String.format("%s%%", value), Type.LIKE);
            default:
                return new ConfigWhereImpl<>(column, String.format("%%%s%%", value), Type.LIKE);
        }
    }

    public static ConfigWhere<ConfigWhere[]> and(ConfigWhere... wheres) {
        return new ConfigWhereImpl<>(Strings.EMPTY, wheres, Type.AND);
    }

    public static ConfigWhere<ConfigWhere[]> or(ConfigWhere... wheres) {
        return new ConfigWhereImpl<>(Strings.EMPTY, wheres, Type.OR);
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

    @Override
    public ConfigWhere<T> rename(String column) {
        return new ConfigWhereImpl<>(column, value, type);
    }
}
