package com.fr.swift.source.etl;

/**
 * Created by pony on 2018/1/5.
 * 操作的类型，union，join等等
 */
public enum OperatorType {
    DETAIL(true), UNION(false), JOIN(false), FILTER(false), COLUMNROWTRANS(false), GROUPSUM(false),
    ONEUNIONRELATION(true), TWOUNIONRELATION(true), COLUMNFORMULA(true), SORT(false), GETDATE(true),
    DATEDIFF(true), ACCUMULATE(true), ALLDATA(true), PERIOD(true), PERCENTAGE(true), RANK(true), CONVERTER(true);

    private boolean isAddColumn;

    OperatorType(boolean isAddColumn) {
        this.isAddColumn = isAddColumn;
    }

    public boolean isAddColumn() {
        return isAddColumn;
    }
}
