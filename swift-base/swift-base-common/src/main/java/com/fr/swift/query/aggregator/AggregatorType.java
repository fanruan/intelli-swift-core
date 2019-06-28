package com.fr.swift.query.aggregator;

/**
 *
 * @author pony
 * @date 2018/3/26
 */
public enum AggregatorType {
    // normal
    SUM, MAX, MIN, AVERAGE, COUNT,
    DISTINCT,
    HLL_DISTINCT,
    STRING_COMBINE,
    DATE_MAX, DATE_MIN, MEDIAN, VARIANCE, STANDARD_DEVIATION,
    DUMMY,

    // extension
    DISTINCT_DATE_YMD,
    TOP_PERCENTILE,

    // FUNNEL
    FUNNEL, FUNNEL_PATHS
}
