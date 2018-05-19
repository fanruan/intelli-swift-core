package com.fr.swift.query.filter;

/**
 * Created by Lyon on 2018/2/2.
 */
public enum SwiftDetailFilterType {

    STRING_IN,
    STRING_LIKE,
    STRING_ENDS_WITH,
    STRING_STARTS_WITH,
    STRING_NOT_IN,
    STRING_NOT_LIKE,
    STRING_NOT_ENDS_WITH,
    STRING_NOT_STARTS_WITH,

    NUMBER_CONTAIN,
    NUMBER_IN_RANGE,
    NUMBER_NOT_CONTAIN,
    NUMBER_NOT_IN_RANGE,
    NUMBER_AVERAGE,

    DATE_IN_RANGE,
    DATE_NOT_IN_RANGE,

    BOTTOM_N,
    TOP_N,

    NULL,
    NOT_NULL,

    AND,
    OR,

    FORMULA,

    NOT_SHOW,
    ALL_SHOW,
    KEY_WORDS,

    /**
     * 需要再次转换的临时类型
     */
    TMP_DATE_BELONG_STRING,
    TMP_DATE_NOT_BELONG_STRING
}
