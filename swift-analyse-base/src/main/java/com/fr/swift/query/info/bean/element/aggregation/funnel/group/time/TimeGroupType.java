package com.fr.swift.query.info.bean.element.aggregation.funnel.group.time;

/**
 * @author yee
 * @date 2019-06-28
 */
public enum TimeGroupType {
    /**
     * Continuous Time Group
     */
    MILLISECONDS,
    SECONDS,
    MINUTES,
    HOURS,
    DAYS,
    MOUTHS,
    YEARS,

    /**
     * Discrete Time Group
     */
    WORK_DAY
}
