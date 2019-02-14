package com.fr.swift.source.alloter.impl.hash;

import com.fr.swift.source.alloter.impl.BaseAllotRule;

/**
 * @author anchore
 * @date 2018/6/5
 */
public class HashAllotRule extends BaseAllotRule {

    private int fieldIndex;
    private int segCount;

    /**
     * TODO: 2018/12/28 realTime导入、属性存入对应表meta
     */

    public HashAllotRule(int fieldIndex, int segCount) {
        super(Integer.MAX_VALUE);
        this.fieldIndex = fieldIndex;
        this.segCount = segCount;
    }

    public int getFieldIndex() {
        return fieldIndex;
    }

    public int getSegCount() {
        return segCount;
    }

    @Override
    public Type getType() {
        return AllotType.HASH;
    }
}