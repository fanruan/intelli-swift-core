package com.fr.swift.query.group;

/**
 * Created by pony on 2017/12/11.
 */
public enum GroupType {
    NONE,
    AUTO,
    CUSTOM,
    CUSTOM_NUMBER,

    YEAR,
    // 季度
    QUARTER,
    MONTH,
    WEEK,
    DAY,
    HOUR,
    MINUTE,
    SECOND,
    // 周数 一年中第几周
    WEEK_OF_YEAR,
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
    // 年 周几
    Y_W,
    // 月 日
    M_D
}
