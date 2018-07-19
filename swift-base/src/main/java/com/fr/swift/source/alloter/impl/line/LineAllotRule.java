package com.fr.swift.source.alloter.impl.line;

import com.fr.swift.source.alloter.impl.BaseAllotRule;

/**
 * @author anchore
 * @date 2018/6/5
 */
public class LineAllotRule extends BaseAllotRule {
    private static final int DEFAULT_STEP = 10000000;

    private int step;

    public LineAllotRule() {
        this(DEFAULT_STEP);
    }

    public LineAllotRule(int step) {
        this.step = step;
    }

    public int getStep() {
        return step;
    }

    @Override
    public Type getType() {
        return Type.LINE;
    }
}