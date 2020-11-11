package com.fr.swift.source.alloter.impl.hash.function;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lucifer
 * @date 2020/2/11
 * @description 默认12个月hash, 特殊数据表+二次hash
 * @since
 */
public class DateAppIdHashFunction extends BaseHashFunction {

    @JsonProperty("partitions")
    private int partitions;

    private static Pattern pattern = Pattern.compile("\\d{6}");

    private static DateTimeFormatter yearMonthFormatter = DateTimeFormatter.ofPattern("yyyyMM");

    public DateAppIdHashFunction() {
    }

    public DateAppIdHashFunction(int partitions) {
        this.partitions = partitions;
    }

    @Override
    public int indexOf(Object key) {
        String hashKey = String.valueOf(key);
        Matcher matcher = pattern.matcher(hashKey);
        if (matcher.matches()) {
            YearMonth yearMonth = YearMonth.parse(hashKey, yearMonthFormatter);
            return yearMonth.getYear() * 100 + yearMonth.getMonthValue();
        }
        int appIdValue = partitions != 0 ? Math.abs(hashKey.hashCode()) % partitions : 0;
        return appIdValue;
    }

    @Override
    public int indexOf(List<Object> keys) {
        return indexOf(keys.get(0)) * 100 + indexOf(keys.get(1));
    }

    @Override
    public HashType getType() {
        return HashType.APPID_YEARMONTH;
    }

    @Override
    public String getCubePath(int logicorder) {
        return String.valueOf(logicorder / 100);
    }
}
