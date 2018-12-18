package com.fr.swift.source.alloter.impl;

import com.fr.swift.source.alloter.AllotRule;

/**
 * @author anchore
 * @date 2018/7/16
 */
public abstract class BaseAllotRule implements AllotRule {

    private AllotType type;

    public BaseAllotRule(AllotType type) {
        this.type = type;
    }

    @Override
    public AllotType getType() {
        return type;
    }

    public enum AllotType implements Type {
        //
        LINE, HASH
    }
}