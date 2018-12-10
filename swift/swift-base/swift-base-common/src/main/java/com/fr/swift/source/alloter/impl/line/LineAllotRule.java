package com.fr.swift.source.alloter.impl.line;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.source.alloter.impl.BaseAllotRule;

/**
 * @author anchore
 * @date 2018/6/5
 */
public class LineAllotRule extends BaseAllotRule {
    public static final int STEP = 10000000, MEM_STEP = 100000;

    @JsonProperty("step")
    private int step;

    public LineAllotRule() {
        this(STEP);
    }

    public LineAllotRule(int step) {
        this.step = step;
    }

    public int getStep() {
        return step;
    }

    @Override
    public AllotType getType() {
        return AllotType.LINE;
    }
}