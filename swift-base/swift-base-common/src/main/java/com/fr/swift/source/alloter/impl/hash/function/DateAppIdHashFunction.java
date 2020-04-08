package com.fr.swift.source.alloter.impl.hash.function;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author lucifer
 * @date 2020/2/11
 * @description 默认12个月hash, 特殊数据表+二次hash
 * @since
 */
public class DateAppIdHashFunction implements HashFunction {

    @JsonProperty("partitions")
    private int partitions;

    private static DateTimeFormatter yearMonthFormatter = DateTimeFormatter.ofPattern("yyyyMM");

    public DateAppIdHashFunction() {
    }

    public DateAppIdHashFunction(int partitions) {
        this.partitions = partitions;
    }

    @Override
    public int indexOf(Object key) {
        String yearMonthStr = String.valueOf(key);
        YearMonth yearMonth = YearMonth.parse(yearMonthStr, yearMonthFormatter);
        return yearMonth.getYear() * 100 + yearMonth.getMonthValue();
    }

    @Override
    public int indexOf(List<Object> keys) {
        String appIdStr = String.valueOf(keys.get(1));
        int appIdValue = partitions != 0 ? Math.abs(appIdStr.hashCode()) % partitions : 0;
        return indexOf(keys.get(0)) * 100 + appIdValue;
    }

    @Override
    public HashType getType() {
        return HashType.YEAR_MONTH;
    }
}
