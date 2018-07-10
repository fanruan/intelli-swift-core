package com.fr.swift.source.alloter.line;

import com.fr.swift.source.alloter.AllotRule;
import com.fr.third.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author anchore
 * @date 2018/6/5
 */
public class LineAllotRule implements AllotRule {
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
    @JsonIgnore
    public Type getType() {
        return Type.LINE;
    }
}