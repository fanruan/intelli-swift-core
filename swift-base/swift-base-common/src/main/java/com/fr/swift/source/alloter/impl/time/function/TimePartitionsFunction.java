package com.fr.swift.source.alloter.impl.time.function;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fr.swift.source.alloter.impl.hash.function.HashFunction;
import com.fr.swift.source.alloter.impl.hash.function.HashType;

import java.util.Calendar;

/**
 * @author Marvin
 * @date 8/2/2019
 * @description
 * @since swift 1.1
 */
public class TimePartitionsFunction implements HashFunction {

    @JsonProperty("partitions")
    TimePartitionsType partitionsType;

    private TimePartitionsFunction() {
    }

    public TimePartitionsFunction(TimePartitionsType partitionsType) {
        this.partitionsType = partitionsType;
    }

    @Override
    public int indexOf(Object key) {
        long time = (key == null) ? 0 : Long.valueOf(String.valueOf(key)).longValue();
        if (time <= 0 || time > System.currentTimeMillis()) {
            time = 0;
        }
        Calendar calendar = Calendar.getInstance();
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
        }
        return index;
    }

    @Override
    public HashType getType() {
        return HashType.TIME;
    }
}
