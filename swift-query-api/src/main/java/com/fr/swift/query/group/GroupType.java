package com.fr.swift.query.group;

/**
 * @author pony
 * @date 2017/12/11
 */
public enum GroupType {
    NONE,
    AUTO,
    CUSTOM,
    CUSTOM_NUMBER,

    CUSTOM_SORT,
    OTHER_DIMENSION_SORT,

    //单值的顺序不要动啊，用来排序

    YEAR,
    // 季度
    QUARTER,
    MONTH,
    // 周数 一年中第几周
    WEEK_OF_YEAR,
    //DAY_OF_WEEK
    WEEK,
    DAY,
    HOUR,
    MINUTE,
    SECOND,
    // 年 月 日 时 分 秒
    Y_M_D_H_M_S,
    // 年 月 日 时 分
    Y_M_D_H_M,
    // 年 月 日 时
    Y_M_D_H,
    // 年 月 日
    Y_M_D,
    // 年 季度
    Y_Q,
    // 年 月
    Y_M,
    // 年 日
    Y_D,
    // 年 周数
    Y_W,
    // 月 日
    M_D
}