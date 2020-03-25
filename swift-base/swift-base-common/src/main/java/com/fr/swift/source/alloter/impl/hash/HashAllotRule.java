package com.fr.swift.source.alloter.impl.hash;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fr.swift.source.alloter.impl.BaseAllotRule;
import com.fr.swift.source.alloter.impl.hash.function.HashFunction;
import com.fr.swift.source.alloter.impl.hash.function.JdkHashFunction;

import java.util.Collections;
import java.util.List;

/**
 * @author anchore
 * @date 2018/6/5
 */
public class HashAllotRule extends BaseAllotRule {
    public static final int HASH_PARTITIONS = 64;

    @JsonProperty("fieldIndexes")
    private List fieldIndexes;

    @JsonProperty("hashFunction")
    private HashFunction hashFunction;

    public HashAllotRule() {
        this(Collections.singletonList(0));
    }

    public HashAllotRule(List fieldIndexes) {
        this(fieldIndexes, HASH_PARTITIONS);
    }

    public HashAllotRule(List fieldIndexes, int partitions) {
        this(fieldIndexes, new JdkHashFunction(partitions));
    }

    public HashAllotRule(List fieldIndexes, int partitions, int capacity) {
        this(fieldIndexes, new JdkHashFunction(partitions), capacity);
    }

    public HashAllotRule(List fieldIndexes, HashFunction hashFunction) {
        this(fieldIndexes, hashFunction, CAPACITY);
    }

    public HashAllotRule(List fieldIndexes, HashFunction hashFunction, int capacity) {
        super(capacity);
        this.fieldIndexes = fieldIndexes;
        this.hashFunction = hashFunction;
    }

    public List getFieldIndexes() {
        return fieldIndexes;
    }

    public HashFunction getHashFunction() {
        return hashFunction;
    }

    @Override
    public Type getType() {
        return AllotType.HASH;
    }
}