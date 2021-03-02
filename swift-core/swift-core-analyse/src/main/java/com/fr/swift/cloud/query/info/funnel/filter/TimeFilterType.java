package com.fr.swift.cloud.query.info.funnel.filter;

import java.util.concurrent.TimeUnit;

/**
 * @author yee
 * @date 2019-06-28
 */
public enum TimeFilterType {
    //
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
