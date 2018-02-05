package com.fr.swift.source.etl;

/**
 * Created by pony on 2018/1/5.
 * 操作的类型，union，join等等
 */
public enum OperatorType {
    DETAIL(true), UNION(false), JOIN(false), FILTER(false), COLUMNROWTRANS(true), GROUPSUM(true), ONEUNIONRELATION(true), TWOUNIONRELATION(true), COLUMNFORMULA(true), SORT(true);

    private boolean isAddColumn;

    OperatorType(boolean isAddColumn) {
        this.isAddColumn = isAddColumn;
    }

    public boolean isAddColumn() {
        return isAddColumn;
    }
}
