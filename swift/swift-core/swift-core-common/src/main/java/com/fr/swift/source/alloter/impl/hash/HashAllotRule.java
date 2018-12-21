package com.fr.swift.source.alloter.impl.hash;

import com.fr.swift.source.alloter.RowAnalyzer;
import com.fr.swift.source.alloter.impl.BaseAllotRule;

/**
 * @author anchore
 * @date 2018/6/5
 */
public class HashAllotRule extends BaseAllotRule {

    public HashAllotRule(int capacity) {
        super(capacity);
    }

    @Override
    public Type getType() {
        return AllotType.HASH;
    }

    @Override
    public RowAnalyzer analyzer() {
        return null;
    }
}