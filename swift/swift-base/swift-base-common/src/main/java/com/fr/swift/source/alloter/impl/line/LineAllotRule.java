package com.fr.swift.source.alloter.impl.line;

import com.fr.swift.source.alloter.impl.BaseAllotRule;

/**
 * @author anchore
 * @date 2018/6/5
 */
public class LineAllotRule extends BaseAllotRule {
    public static final int STEP = CAPACITY, MEM_STEP = MEM_CAPACITY;

    public LineAllotRule(int capacity) {
        super(capacity);
    }

    @Override
    public Type getType() {
        return AllotType.LINE;
    }

}