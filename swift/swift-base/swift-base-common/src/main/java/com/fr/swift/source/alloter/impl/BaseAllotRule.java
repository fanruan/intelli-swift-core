package com.fr.swift.source.alloter.impl;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.source.alloter.AllotRule;
import com.fr.swift.util.Assert;

/**
 * @author anchore
 * @date 2018/7/16
 */
public abstract class BaseAllotRule implements AllotRule {

    public static final int CAPACITY = 10000000, MEM_CAPACITY = 100000;

    @JsonProperty("capacity")
    private int capacity;

    public BaseAllotRule(int capacity) {
        Assert.isTrue(capacity > 0);

        this.capacity = capacity;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    public enum AllotType implements Type {
        //
        LINE, HASH
    }
}