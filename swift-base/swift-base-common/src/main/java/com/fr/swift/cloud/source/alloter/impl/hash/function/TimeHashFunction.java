package com.fr.swift.cloud.source.alloter.impl.hash.function;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Calendar;
import java.util.List;

/**
 * @author Marvin
 * @date 8/5/2019
 * @description
 * @since swift 1.1
 */

public class TimeHashFunction extends BaseHashFunction {

    @JsonProperty("partitionstype")
    private TimeType partitionsType;

    private Calendar calendar = Calendar.getInstance();

    public TimeHashFunction() {
        this(TimeType.YEAR_MONTH);
    }

    public TimeHashFunction(TimeType partitionsType) {
        this.partitionsType = partitionsType;
    }

    @Override
    public int indexOf(Object key) {
        long time = (key == null) ? 0 : Long.parseLong(String.valueOf(key));
        if (time <= 0 || time > System.currentTimeMillis()) {
            time = 0;
        }
        calendar.setTimeInMillis(time);
        int index = 0;
        switch (partitionsType) {
            case YEAR:
                index = calendar.get(Calendar.YEAR);
                break;
            case QUARTER:
                index = calendar.get(Calendar.MONTH) / 3;
                break;
            case MONTH:
                index = calendar.get(Calendar.MONTH);
                break;
            case YEAR_MONTH:
                index = calendar.get(Calendar.YEAR) * 100 + calendar.get(Calendar.MONTH);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + partitionsType);
        }
        return index;
    }

    @Override
    public int indexOf(List<Object> keys) {
        return 0;
    }

    @Override
    public HashType getType() {
        return HashType.TIME;
    }
}