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

    DATE_IN_RANGE,
    DATE_NOT_IN_RANGE,

    BOTTOM_N,
    TOP_N,

    NULL,
    NOT_NULL,

    AND,
    OR,

    FORMULA,

    ALL_SHOW,
    KEY_WORDS
}
