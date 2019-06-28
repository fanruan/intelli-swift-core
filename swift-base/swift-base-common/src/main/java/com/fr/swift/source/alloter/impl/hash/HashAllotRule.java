package com.fr.swift.source.alloter.impl.hash;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.source.alloter.impl.BaseAllotRule;
import com.fr.swift.source.alloter.impl.hash.function.HashFunction;
import com.fr.swift.source.alloter.impl.hash.function.JdkHashFunction;

/**
 * @author anchore
 * @date 2018/6/5
 */
public class HashAllotRule extends BaseAllotRule {
    public static final int HASH_PARTITIONS = 64;

    @JsonProperty("fieldIndex")
    private int fieldIndex;

    @JsonProperty("hashFunction")
    private HashFunction hashFunction;

    public HashAllotRule() {
        this(0);
    }

    public HashAllotRule(int fieldIndex) {
        this(fieldIndex, HASH_PARTITIONS);
    }

    public HashAllotRule(int fieldIndex, int partitions) {
        this(fieldIndex, new JdkHashFunction(partitions));
    }

    public HashAllotRule(int fieldIndex, int partitions, int capacity) {
        this(fieldIndex, new JdkHashFunction(partitions), capacity);
    }

    public HashAllotRule(int fieldIndex, HashFunction hashFunction) {
        this(fieldIndex, hashFunction, CAPACITY);
    }

    public HashAllotRule(int fieldIndex, HashFunction hashFunction, int capacity) {
        super(capacity);
        this.fieldIndex = fieldIndex;
        this.hashFunction = hashFunction;
    }

    public int getFieldIndex() {
        return fieldIndex;
    }

    public HashFunction getHashFunction() {
        return hashFunction;
    }

    @Override
    public Type getType() {
        return AllotType.HASH;
    }
}