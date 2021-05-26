package com.fr.swift.cloud.source.alloter.impl.hash.function;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fr.swift.cloud.util.TimeUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Heng.J
 * @date 2021/5/11
 * @description
 * @since swift-1.2.0
 */
public class DesignerHashFunction extends BaseHashFunction {
    // 可以和 DateAppIdHashFunction 合并

    private static final int COMPOSE_MAGIC = 1000;

    @JsonProperty("partitions")
    private int partitions;

    public DesignerHashFunction() {
    }

    public DesignerHashFunction(int partitions) {
        this.partitions = partitions;
    }

    /**
     * for single designerId or yearMonth or yearMonthDate
     */
    @Override
    public int indexOf(Object key) {
        String hashKey = String.valueOf(key);
        if (TimeUtils.isYearMonth(hashKey)) {
            return Integer.parseInt(hashKey);
        } else if (TimeUtils.isYearMonthDate(hashKey)) {
            return indexOf(hashKey.substring(0, 6));
        }
        return partitions != 0 ? Math.abs(hashKey.hashCode()) % partitions : 0;
    }

    /**
     * for designerId and yearMonthDate
     * key[0]: yearMonthDate
     * key[1]: designerId
     * key[2]: yearMonth(not use)
     */
    @Override
    public int indexOf(List<Object> keys) {
        return indexOf(keys.get(0)) * COMPOSE_MAGIC + indexOf(keys.get(1));
    }

    @Override
    public HashType getType() {
        return HashType.DESIGNER;
    }

    @Override
    public String getCubePath(int logicOrder) {
        return String.valueOf(logicOrder / COMPOSE_MAGIC);
    }

    @Override
    public List<Integer> divideOf(int index) {
        return Stream.of(index / COMPOSE_MAGIC, index % COMPOSE_MAGIC).collect(Collectors.toList());
    }
}
