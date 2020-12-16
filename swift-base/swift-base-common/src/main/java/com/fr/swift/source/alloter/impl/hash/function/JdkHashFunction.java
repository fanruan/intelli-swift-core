package com.fr.swift.source.alloter.impl.hash.function;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author lucifer
 * @date 2019-06-24
 * @description
 * @since advanced swift 1.0
 */
public class JdkHashFunction extends BaseHashFunction {

    @JsonProperty("partitions")
    private int partitions;

    private JdkHashFunction() {
    }

    public JdkHashFunction(int partitions) {
        this.partitions = partitions;
    }

    @Override
    public int indexOf(Object key) {
        int h;
        int hashCode = (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
        int index = (partitions - 1) & hashCode;
        return index;
    }

    @Override
    public int indexOf(List<Object> keys) {
        return 0;
    }

    @Override
    public HashType getType() {
        return HashType.JDK;
    }
}