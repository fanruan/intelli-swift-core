package com.fr.swift.query.filter;

/**
 * @author Lyon
 * @date 2018/2/2
 */
public enum SwiftDetailFilterType {
    //
    STRING_LIKE,
    STRING_ENDS_WITH,
    STRING_STARTS_WITH,

    NUMBER_IN_RANGE,
    NUMBER_AVERAGE,

    IN,

    BOTTOM_N,
    TOP_N,

    NULL,

    AND,
    OR,
    NOT,

    FORMULA,

    EMPTY,
    ALL_SHOW,
    KEY_WORDS,

    /**
     * 需要再次转换的临时类型
     */
    TMP_DATE_BELONG_STRING,
    TMP_DATE_NOT_BELONG_STRING
}
