package com.fr.swift.source.alloter.impl.hash;

import com.fr.swift.source.alloter.RowAnalyzer;
import com.fr.swift.source.alloter.impl.BaseAllotRule;

/**
 * @author anchore
 * @date 2018/6/5
 */
public class HashAllotRule extends BaseAllotRule {

    // TODO: 2018/12/28 realTime导入、属性存入对应表meta
    private static final int capacity = Integer.MAX_VALUE;

    private RowAnalyzer analyzer;

    public HashAllotRule(int fieldIndex, int segCount) {
        super(capacity);
        this.analyzer = new HashRowAnalyzer(fieldIndex, segCount);
    }

    @Override
    public Type getType() {
        return AllotType.HASH;
    }

    @Override
    public RowAnalyzer analyzer() {
        return analyzer;
    }
}