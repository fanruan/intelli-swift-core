package com.fr.swift.query.info.bean.type.cal;

/**
 * Created by Lyon on 2018/4/8.
 */
public enum CalTargetType {

    ALL_SUM_OF_ABOVE,
    ALL_SUM_OF_ALL,
    ALL_AVG,
    ALL_MIN,
    ALL_MAX,
    ALL_RANK_ASC,
    ALL_RANK_DEC,
    GROUP_RANK_ASC,
    GROUP_RANK_DEC,
    GROUP_SUM_OF_ALL,
    GROUP_AVG,
    GROUP_MIN,
    GROUP_MAX,
    GROUP_SUM_OF_ABOVE,

    DIMENSION_PERCENT,
    TARGET_PERCENT,

    BROTHER_VALUE,
    BROTHER_RATE,
    COUSIN_VALUE,
    COUSIN_RATE,

    FORMULA,

    /**
     * 简单的加减乘除不走公式了
     */
    ARITHMETIC_ADD,
    ARITHMETIC_SUB,
    ARITHMETIC_MUL,
    ARITHMETIC_DIV,
}
