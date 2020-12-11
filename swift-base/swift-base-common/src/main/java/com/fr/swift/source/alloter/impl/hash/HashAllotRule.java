package com.fr.swift.source.alloter.impl.hash;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fr.swift.source.alloter.impl.BaseAllotRule;
import com.fr.swift.source.alloter.impl.hash.function.HashFunction;
import com.fr.swift.source.alloter.impl.hash.function.JdkHashFunction;

/**
 * @author anchore
 * @date 2018/6/5
 */
public class HashAllotRule extends BaseAllotRule {
    public static final int HASH_PARTITIONS = 64;

    @JsonProperty("fieldIndexes")
    private int[] fieldIndexes;

    @JsonProperty("hashFunction")
    private HashFunction hashFunction;

    public HashAllotRule() {
        this(new int[]{0});
    }

    public HashAllotRule(int[] fieldIndexes) {
        this(fieldIndexes, HASH_PARTITIONS);
    }

    public HashAllotRule(int[] fieldIndexes, int partitions) {
        this(fieldIndexes, new JdkHashFunction(partitions));
    }

    public HashAllotRule(int[] fieldIndexes, int partitions, int capacity) {
        this(fieldIndexes, new JdkHashFunction(partitions), capacity);
    }

    public HashAllotRule(int[] fieldIndexes, HashFunction hashFunction) {
        this(fieldIndexes, hashFunction, CAPACITY);
    }

    public HashAllotRule(int[] fieldIndexes, HashFunction hashFunction, int capacity) {
        super(capacity);
        this.fieldIndexes = fieldIndexes;
        this.hashFunction = hashFunction;
    }

    public int[] getFieldIndexes() {
        return fieldIndexes;
    }

    public HashFunction getHashFunction() {
        return hashFunction;
    }

    @Override
    public Type getType() {
        return AllotType.HASH;
    }

    @Override
    public String getCubePath(int order) {
        return hashFunction.getCubePath(order);
    }
}