package com.fr.swift.source.alloter.impl.time;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fr.swift.source.alloter.impl.hash.HashAllotRule;
import com.fr.swift.source.alloter.impl.hash.function.HashFunction;
import com.fr.swift.source.alloter.impl.time.function.TimePartitionsFunction;
import com.fr.swift.source.alloter.impl.time.function.TimePartitionsType;

/**
 * @author Marvin.Zhao
 * @date 2019/7/17
 */
public class TimeAllotRule extends HashAllotRule {

    public static final TimePartitionsType PARTITION_TYPE = TimePartitionsType.YEAR;

    @JsonProperty("fieldIndex")
    private int fieldIndex;

    @JsonProperty("timePartitionsFunction")
    private HashFunction timePartitionsFunction;

    public TimeAllotRule() {
        this(0);
    }

    public TimeAllotRule(int fieldIndex) {
        this(fieldIndex, PARTITION_TYPE);
    }

    public TimeAllotRule(int fieldIndex, TimePartitionsType type) {
        this(fieldIndex, new TimePartitionsFunction(type));
    }

    public TimeAllotRule(int fieldIndex, TimePartitionsType type, int capacity) {
        this(fieldIndex, new TimePartitionsFunction(type), capacity);
    }

    public TimeAllotRule(int fieldIndex, HashFunction timePartitionsFunction) {
        this(fieldIndex, timePartitionsFunction, CAPACITY);
    }

    public TimeAllotRule(int fieldIndex, HashFunction timePartitionsFunction, int capacity) {
        super(capacity);
        this.fieldIndex = fieldIndex;
        this.timePartitionsFunction = timePartitionsFunction;
    }

    public int getFieldIndex() {
        return fieldIndex;
    }

    public HashFunction getHashFunction() {
        return timePartitionsFunction;
    }

    @Override
    public Type getType() {
        return AllotType.TIME;
    }
}