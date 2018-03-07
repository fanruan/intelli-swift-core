package com.fr.swift.source.etl;

/**
 * @author pony
 * @date 2018/1/5
 * 操作的类型，union，join等等
 */
public enum OperatorType {
<<<<<<< HEAD
    DETAIL(true), UNION(false), JOIN(false), FILTER(false), COLUMNROWTRANS(false), GROUPSUM(false),
    ONEUNIONRELATION(true), TWOUNIONRELATION(true), COLUMNFORMULA(true), SORT(false), GETDATE(true),
    DATEDIFF(true), ACCUMULATE(true), ALLDATA(true), PERIOD(true), PERCENTAGE(true), RANK(true), CONVERTER(true);
=======
    DETAIL(true), UNION(false), JOIN(false), FILTER(false),
    COLUMN_ROW_TRANS(false), GROUP_SUM(false), ONE_UNION_RELATION(true),
    TWO_UNION_RELATION(true), COLUMN_FORMULA(true), SORT(false);
>>>>>>> 22aeba7cc2ad9fe87e1f557423de16040be8b1a5

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
