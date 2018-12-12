package com.fr.swift.source.alloter.impl;

import com.fr.swift.source.alloter.AllotRule;

/**
 * @author anchore
 * @date 2018/7/16
 */
public abstract class BaseAllotRule implements AllotRule {
    @Override
    public abstract AllotType getType();

    public enum AllotType implements Type {
        //
        LINE, HASH
    }
}