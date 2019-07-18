package com.fr.swift.query.info.funnel.filter;

import java.util.concurrent.TimeUnit;

/**
 * @author yee
 * @date 2019-06-28
 */
public enum TimeFilterType {
    /**
     * 一个月以30天计算
     */
    MONTH(TimeUnit.DAYS),
    DAY(TimeUnit.DAYS),
    HOER(TimeUnit.HOURS),
    MINUTE(TimeUnit.MINUTES);
    private TimeUnit timeUnit;

    TimeFilterType(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }
}
