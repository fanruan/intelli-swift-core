package com.fr.swift.cloud.source.alloter.impl.hash.function;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fr.swift.cloud.source.alloter.impl.hash.HashIndexRange;
import com.fr.swift.cloud.util.TimeUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author lucifer
 * @date 2020/2/11
 * @description 默认12个月hash, 特殊数据表+二次hash
 * @since
 */
public class DateAppIdHashFunction extends BaseHashFunction {

    @JsonProperty("partitions")
    private int partitions;

    private static final int COMPOSE_MAGIC = 100;

    public DateAppIdHashFunction() {
    }

    public DateAppIdHashFunction(int partitions) {
        this.partitions = partitions;
    }

    @Override
    public int indexOf(Object key) {
        String hashKey = String.valueOf(key);
        if (TimeUtils.isYearMonth(hashKey)) {
            return Integer.parseInt(hashKey);
        }
        return partitions != 0 ? Math.abs(hashKey.hashCode()) % partitions : 0;
    }

    @Override
    public int indexOf(List<Object> keys) {
        return indexOf(keys.get(0)) * COMPOSE_MAGIC + indexOf(keys.get(1));
    }

    @Override
    public HashType getType() {
        return HashType.APPID_YEARMONTH;
    }

    @Override
    public String getCubePath(int logicOrder) {
        return String.valueOf(logicOrder / COMPOSE_MAGIC);
    }

    @Override
    public List<Integer> divideOf(int index) {
        return Stream.of(index / COMPOSE_MAGIC, index % COMPOSE_MAGIC).collect(Collectors.toList());
    }

    public static class DateAppIdHashRange implements HashIndexRange {

        private Integer hashValue;

        public DateAppIdHashRange() {
        }

        @Override
        public DateAppIdHashRange ofKey(Object hashValue) {
            this.hashValue = Integer.parseInt(String.valueOf(hashValue));
            return this;
        }

        @Override
        public Integer getBegin() {
            return hashValue * COMPOSE_MAGIC;
        }

        @Override
        public Integer getEnd() {
            return (hashValue + 1) * COMPOSE_MAGIC;
        }
    }
}
