package com.fr.swift.source.etl;

/**
 * @author pony
 * @date 2018/1/5
 * 操作的类型，union，join等等
 */
public enum OperatorType {
    DETAIL(true),
    UNION(false),
    JOIN(false),
    FILTER(false),
    COLUMN_ROW_TRANS(false),
    GROUP_SUM(false),
    ONE_UNION_RELATION(true),
    TWO_UNION_RELATION(true),
    COLUMN_FORMULA(true),
    SORT(false),
    GET_DATE(true),
    DATE_DIFF(true),
    ACCUMULATE(true),
    ALL_DATA(true),
    PERIOD(true),
    PERCENTAGE(true),
    RANK(true),
    CONVERTER(true),
    EXPRESSION_FILTER(true),
    GROUP_STRING(true),
    GROUP_NUM(true),
    EXTRA_TRUE(true),
    EXTRA_FALSE(false),
    R_COMPILE(false);

    private boolean isAddColumn;

    OperatorType(boolean isAddColumn) {
        this.isAddColumn = isAddColumn;
    }

    /**
     * @return 是否为新增列
     */
    public boolean isAddColumn() {
        return isAddColumn;
    }
}