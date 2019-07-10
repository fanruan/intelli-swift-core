package com.fr.swift.query.query.funnel;

import java.util.concurrent.TimeUnit;

/**
 * @author yee
 * @date 2019-06-28
 */
public class TimeWindowBean {
    private int duration;
    private TimeUnit unit;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }

    public long toMillis() {
        return unit.toMillis(duration);
    }
}
