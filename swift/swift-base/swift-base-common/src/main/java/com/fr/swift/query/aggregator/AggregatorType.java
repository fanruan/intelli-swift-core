package com.fr.swift.query.aggregator;

/**
 * Created by pony on 2018/3/26.
 */
public enum AggregatorType {
    SUM, MAX, MIN, AVERAGE, COUNT,
    DISTINCT,
    HLL_DISTINCT,
    STRING_COMBINE,
    DATE_MAX, DATE_MIN, MEDIAN, VARIANCE, STANDARD_DEVIATION,
    DUMMY,

    // extension
    DISTINCT_DATE_YMD,
    TOP_PERCENTILE
}
